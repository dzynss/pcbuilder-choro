package com.pcbuilder.ms_despachos.service;

import com.pcbuilder.ms_despachos.client.UsuarioClient;
import com.pcbuilder.ms_despachos.dto.DespachoRequestDTO;
import com.pcbuilder.ms_despachos.dto.DespachoResponseDTO;
import com.pcbuilder.ms_despachos.entity.Despacho;
import com.pcbuilder.ms_despachos.exception.ErrorComunicacionException;
import com.pcbuilder.ms_despachos.exception.EstadoInvalidoException;
import com.pcbuilder.ms_despachos.exception.RecursoNoEncontradoException;
import com.pcbuilder.ms_despachos.repository.DespachoRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DespachoService {

    private static final Set<String> ESTADOS_VALIDOS = Set.of("BODEGA", "EN_RUTA", "ENTREGADO");

    private final DespachoRepository repo;
    private final UsuarioClient usuarioClient;

    public List<DespachoResponseDTO> listarTodos() {
        return repo.findAll().stream().map(this::aResponseDTO).toList();
    }

    public DespachoResponseDTO buscarPorId(Long id) {
        return aResponseDTO(buscarEntidadPorId(id));
    }

    public DespachoResponseDTO guardar(DespachoRequestDTO dto) {
        validarUsuarioExiste(dto.idUsuario());

        Despacho despacho = new Despacho();
        despacho.setIdUsuario(dto.idUsuario());
        despacho.setDireccionEnvio(dto.direccionEnvio());
        despacho.setEmpresaTransporte(dto.empresaTransporte());
        despacho.setEstadoSeguimiento("BODEGA");
        despacho.setFechaDespacho(LocalDateTime.now());

        return aResponseDTO(repo.save(despacho));
    }

    public DespachoResponseDTO actualizarEstado(Long id, String nuevoEstado) {
        if (nuevoEstado == null || !ESTADOS_VALIDOS.contains(nuevoEstado.toUpperCase())) {
            throw new EstadoInvalidoException(
                    "El estado '" + nuevoEstado + "' no es válido. Los estados permitidos son: " + ESTADOS_VALIDOS);
        }
        Despacho despacho = buscarEntidadPorId(id);
        despacho.setEstadoSeguimiento(nuevoEstado.toUpperCase());
        return aResponseDTO(repo.save(despacho));
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            throw new RecursoNoEncontradoException("El despacho con ID " + id + " no existe.");
        }
        repo.deleteById(id);
    }

    private void validarUsuarioExiste(Long idUsuario) {
        try {
            usuarioClient.buscarPorId(idUsuario);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("El usuario " + idUsuario + " no existe.");
        } catch (FeignException e) {
            throw new ErrorComunicacionException("ms-usuarios no respondió correctamente: " + e.getMessage());
        }
    }

    private Despacho buscarEntidadPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El despacho con ID " + id + " no existe."));
    }

    private DespachoResponseDTO aResponseDTO(Despacho d) {
        return new DespachoResponseDTO(d.getId(), d.getIdUsuario(), d.getDireccionEnvio(),
                d.getEmpresaTransporte(), d.getEstadoSeguimiento(), d.getFechaDespacho());
    }
}
