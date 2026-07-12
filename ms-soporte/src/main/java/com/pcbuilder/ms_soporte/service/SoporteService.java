package com.pcbuilder.ms_soporte.service;

import com.pcbuilder.ms_soporte.client.ComponenteClient;
import com.pcbuilder.ms_soporte.client.UsuarioClient;
import com.pcbuilder.ms_soporte.dto.TicketRequestDTO;
import com.pcbuilder.ms_soporte.dto.TicketResponseDTO;
import com.pcbuilder.ms_soporte.entity.TicketSoporte;
import com.pcbuilder.ms_soporte.exception.ErrorComunicacionException;
import com.pcbuilder.ms_soporte.exception.EstadoInvalidoException;
import com.pcbuilder.ms_soporte.exception.RecursoNoEncontradoException;
import com.pcbuilder.ms_soporte.repository.TicketRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Lógica de negocio de tickets de soporte. Persiste vía {@link TicketRepository} y valida
 * referencias a otros microservicios (ms-usuarios, ms-componentes) vía Feign antes de
 * crear/actualizar un ticket, en vez de confiar en el request.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SoporteService {

    private final TicketRepository repo;
    private final UsuarioClient usuarioClient;
    private final ComponenteClient componenteClient;

    /** Devuelve todos los tickets registrados, mapeados a DTO de respuesta. */
    public List<TicketResponseDTO> listarTodos() {
        log.info("Listando todos los tickets de soporte");
        return repo.findAll().stream().map(this::aResponseDTO).toList();
    }

    /** Busca un ticket por ID; lanza RecursoNoEncontradoException (→ 404) si no existe. */
    public TicketResponseDTO buscarPorId(Long id) {
        log.info("Buscando el ticket con ID: {}", id);
        return aResponseDTO(buscarEntidadPorId(id));
    }

    /**
     * Crea un ticket nuevo en estado ABIERTO. Antes de guardar valida, vía Feign, que el
     * usuario y el componente referenciados existan realmente en ms-usuarios/ms-componentes.
     */
    public TicketResponseDTO guardar(TicketRequestDTO dto) {
        log.info("Abriendo un ticket nuevo del usuario ID: {} por el componente ID: {}", dto.idUsuario(), dto.idComponente());
        validarUsuarioExiste(dto.idUsuario());
        validarComponenteExiste(dto.idComponente());

        TicketSoporte ticket = new TicketSoporte();
        ticket.setIdUsuario(dto.idUsuario());
        ticket.setIdComponente(dto.idComponente());
        ticket.setDescripcion(dto.descripcion());
        ticket.setEstado("ABIERTO");
        ticket.setFechaCreacion(LocalDateTime.now());

        TicketSoporte guardado = repo.save(ticket);
        log.info("Ticket guardado con ID: {}", guardado.getId());
        return aResponseDTO(guardado);
    }

    /**
     * Actualiza descripción/usuario/componente de un ticket existente, revalidando
     * usuario y componente vía Feign igual que en {@link #guardar}.
     */
    public TicketResponseDTO actualizar(Long id, TicketRequestDTO dto) {
        log.info("Actualizando el ticket con ID: {}", id);
        TicketSoporte ticket = buscarEntidadPorId(id);

        validarUsuarioExiste(dto.idUsuario());
        validarComponenteExiste(dto.idComponente());

        ticket.setIdUsuario(dto.idUsuario());
        ticket.setIdComponente(dto.idComponente());
        ticket.setDescripcion(dto.descripcion());

        TicketSoporte actualizado = repo.save(ticket);
        log.info("Ticket con ID: {} actualizado", actualizado.getId());
        return aResponseDTO(actualizado);
    }

    /** Cierra un ticket abierto; lanza EstadoInvalidoException (→ 409) si ya estaba cerrado. */
    public TicketResponseDTO cerrarTicket(Long id) {
        log.info("Cerrando el ticket con ID: {}", id);
        TicketSoporte ticket = buscarEntidadPorId(id);
        if ("CERRADO".equals(ticket.getEstado())) {
            throw new EstadoInvalidoException("El ticket " + id + " ya está cerrado.");
        }
        ticket.setEstado("CERRADO");
        return aResponseDTO(repo.save(ticket));
    }

    /** Elimina un ticket; lanza RecursoNoEncontradoException (→ 404) si el ID no existe. */
    public void eliminar(Long id) {
        log.info("Eliminando el ticket con ID: {}", id);
        if (!repo.existsById(id)) {
            throw new RecursoNoEncontradoException("El ticket con ID " + id + " no existe.");
        }
        repo.deleteById(id);
    }

    /**
     * Llama a {@link UsuarioClient} para confirmar que el usuario exista en ms-usuarios.
     * Un 404 de Feign se traduce a RecursoNoEncontradoException; cualquier otro fallo de
     * comunicación (timeout, 5xx, etc.) se traduce a ErrorComunicacionException.
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

    /**
     * Llama a {@link ComponenteClient} para confirmar que el componente exista en ms-componentes.
     * Un 404 de Feign se traduce a RecursoNoEncontradoException; cualquier otro fallo de
     * comunicación se traduce a ErrorComunicacionException.
     */
    private void validarComponenteExiste(Long idComponente) {
        log.info("Validando que el componente ID: {} exista en ms-componentes", idComponente);
        try {
            componenteClient.buscarPorId(idComponente);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("El componente " + idComponente + " no existe.");
        } catch (FeignException e) {
            throw new ErrorComunicacionException("ms-componentes no respondió correctamente: " + e.getMessage());
        }
    }

    /** Busca la entidad ticket por ID o lanza RecursoNoEncontradoException; helper interno reutilizado por varios métodos. */
    private TicketSoporte buscarEntidadPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El ticket con ID " + id + " no existe."));
    }

    /** Convierte la entidad TicketSoporte al DTO de respuesta expuesto por el controller. */
    private TicketResponseDTO aResponseDTO(TicketSoporte t) {
        return new TicketResponseDTO(t.getId(), t.getIdUsuario(), t.getIdComponente(),
                t.getDescripcion(), t.getEstado(), t.getFechaCreacion());
    }
}
