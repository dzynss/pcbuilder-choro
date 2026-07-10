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

@Service
@RequiredArgsConstructor
@Slf4j
public class SoporteService {

    private final TicketRepository repo;
    private final UsuarioClient usuarioClient;
    private final ComponenteClient componenteClient;

    public List<TicketResponseDTO> listarTodos() {
        log.info("Listando todos los tickets de soporte");
        return repo.findAll().stream().map(this::aResponseDTO).toList();
    }

    public TicketResponseDTO buscarPorId(Long id) {
        log.info("Buscando el ticket con ID: {}", id);
        return aResponseDTO(buscarEntidadPorId(id));
    }

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

    public TicketResponseDTO cerrarTicket(Long id) {
        log.info("Cerrando el ticket con ID: {}", id);
        TicketSoporte ticket = buscarEntidadPorId(id);
        if ("CERRADO".equals(ticket.getEstado())) {
            throw new EstadoInvalidoException("El ticket " + id + " ya está cerrado.");
        }
        ticket.setEstado("CERRADO");
        return aResponseDTO(repo.save(ticket));
    }

    public void eliminar(Long id) {
        log.info("Eliminando el ticket con ID: {}", id);
        if (!repo.existsById(id)) {
            throw new RecursoNoEncontradoException("El ticket con ID " + id + " no existe.");
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

    private TicketSoporte buscarEntidadPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El ticket con ID " + id + " no existe."));
    }

    private TicketResponseDTO aResponseDTO(TicketSoporte t) {
        return new TicketResponseDTO(t.getId(), t.getIdUsuario(), t.getIdComponente(),
                t.getDescripcion(), t.getEstado(), t.getFechaCreacion());
    }
}
