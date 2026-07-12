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

/**
 * Lógica de negocio de despachos: CRUD sobre DespachoRepository y validación cruzada del usuario
 * destinatario contra ms-usuarios a través de UsuarioClient (Feign). Consumida por DespachoController.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DespachoService {

    /** Únicos valores de estadoSeguimiento aceptados por actualizarEstado. */
    private static final Set<String> ESTADOS_VALIDOS = Set.of("BODEGA", "EN_RUTA", "ENTREGADO");

    private final DespachoRepository repo;
    private final UsuarioClient usuarioClient;

    /** Devuelve todos los despachos existentes, mapeados a DTO de respuesta. */
    public List<DespachoResponseDTO> listarTodos() {
        log.info("Listando todos los despachos");
        return repo.findAll().stream().map(this::aResponseDTO).toList();
    }

    /** Busca un despacho por ID; lanza RecursoNoEncontradoException (404) si no existe. */
    public DespachoResponseDTO buscarPorId(Long id) {
        log.info("Buscando el despacho con ID: {}", id);
        return aResponseDTO(buscarEntidadPorId(id));
    }

    /**
     * Crea un despacho nuevo con estado inicial "BODEGA". Antes de persistir valida
     * (vía Feign a ms-usuarios) que el usuario destinatario exista.
     */
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

    /**
     * Actualiza dirección/empresa de transporte (y usuario) de un despacho existente,
     * revalidando de nuevo al usuario destinatario contra ms-usuarios.
     */
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

    /**
     * Cambia el estado de seguimiento; valida contra ESTADOS_VALIDOS (EstadoInvalidoException -> 400 si no matchea)
     * y contra la existencia del despacho (RecursoNoEncontradoException -> 404).
     */
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

    /** Elimina un despacho por ID; lanza RecursoNoEncontradoException (404) si no existe. */
    public void eliminar(Long id) {
        log.info("Eliminando el despacho con ID: {}", id);
        if (!repo.existsById(id)) {
            throw new RecursoNoEncontradoException("El despacho con ID " + id + " no existe.");
        }
        repo.deleteById(id);
    }

    /**
     * Llama a UsuarioClient (Feign, ms-usuarios) para confirmar que el usuario existe.
     * Un 404 de ms-usuarios se traduce a RecursoNoEncontradoException; cualquier otro fallo
     * de comunicación se traduce a ErrorComunicacionException (502 vía GlobalExceptionHandler).
     */
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

    /** Helper interno: busca la entidad Despacho por ID o lanza RecursoNoEncontradoException. */
    private Despacho buscarEntidadPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El despacho con ID " + id + " no existe."));
    }

    /** Mapea la entidad Despacho al DTO de respuesta expuesto por el controller. */
    private DespachoResponseDTO aResponseDTO(Despacho d) {
        return new DespachoResponseDTO(d.getId(), d.getIdUsuario(), d.getDireccionEnvio(),
                d.getEmpresaTransporte(), d.getEstadoSeguimiento(), d.getFechaDespacho());
    }
}
