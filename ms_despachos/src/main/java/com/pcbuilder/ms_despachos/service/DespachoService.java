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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class DespachoService {

    private static final Set<String> ESTADOS_VALIDOS = Set.of("BODEGA", "EN_RUTA", "ENTREGADO");

    private final DespachoRepository repo;
    private final UsuarioClient usuarioClient;

    public List<DespachoResponseDTO> listarTodos() {
        log.info("Listando todos los despachos");
        return repo.findAll().stream().map(this::aResponseDTO).toList();
    }

    public DespachoResponseDTO buscarPorId(Long id) {
        log.info("Buscando el despacho con ID: {}", id);
        return aResponseDTO(buscarEntidadPorId(id));
    }

    public DespachoResponseDTO guardar(DespachoRequestDTO dto) {
        log.info("Registrando una encomienda nueva para el usuario ID: {}", dto.idUsuario());
        validarUsuarioExiste(dto.idUsuario());

        Despacho despacho = new Despacho();
        despacho.setIdUsuario(dto.idUsuario());
        despacho.setDireccionEnvio(dto.direccionEnvio());
        despacho.setEmpresaTransporte(dto.empresaTransporte());
        despacho.setEstadoSeguimiento("BODEGA");
        despacho.setFechaDespacho(LocalDateTime.now());

        Despacho guardado = repo.save(despacho);
        log.info("Despacho guardado con ID: {}", guardado.getId());
        return aResponseDTO(guardado);
    }

    public DespachoResponseDTO actualizar(Long id, DespachoRequestDTO dto) {
        log.info("Actualizando el despacho con ID: {}", id);
        Despacho despacho = buscarEntidadPorId(id);

        validarUsuarioExiste(dto.idUsuario());

        despacho.setIdUsuario(dto.idUsuario());
        despacho.setDireccionEnvio(dto.direccionEnvio());
        despacho.setEmpresaTransporte(dto.empresaTransporte());

        Despacho actualizado = repo.save(despacho);
        log.info("Despacho con ID: {} actualizado", actualizado.getId());
        return aResponseDTO(actualizado);
    }

    public DespachoResponseDTO actualizarEstado(Long id, String nuevoEstado) {
        log.info("Actualizando el estado del despacho ID: {} a: {}", id, nuevoEstado);
        if (nuevoEstado == null || !ESTADOS_VALIDOS.contains(nuevoEstado.toUpperCase())) {
            throw new EstadoInvalidoException(
                    "El estado '" + nuevoEstado + "' no es válido. Los estados permitidos son: " + ESTADOS_VALIDOS);
        }
        Despacho despacho = buscarEntidadPorId(id);
        despacho.setEstadoSeguimiento(nuevoEstado.toUpperCase());
        return aResponseDTO(repo.save(despacho));
    }

    public void eliminar(Long id) {
        log.info("Eliminando el despacho con ID: {}", id);
        if (!repo.existsById(id)) {
            throw new RecursoNoEncontradoException("El despacho con ID " + id + " no existe.");
        }
        repo.deleteById(id);
    }

    private void validarUsuarioExiste(Long idUsuario) {
        log.info("Validando que el usuario ID: {} exista en ms-usuarios", idUsuario);
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
