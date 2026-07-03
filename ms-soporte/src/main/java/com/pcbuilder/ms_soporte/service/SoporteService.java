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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SoporteService {

    private final TicketRepository repo;
    private final UsuarioClient usuarioClient;
    private final ComponenteClient componenteClient;

    public List<TicketResponseDTO> listarTodos() {
        return repo.findAll().stream().map(this::aResponseDTO).toList();
    }

    public TicketResponseDTO buscarPorId(Long id) {
        return aResponseDTO(buscarEntidadPorId(id));
    }

    public TicketResponseDTO guardar(TicketRequestDTO dto) {
        validarUsuarioExiste(dto.idUsuario());
        validarComponenteExiste(dto.idComponente());

        TicketSoporte ticket = new TicketSoporte();
        ticket.setIdUsuario(dto.idUsuario());
        ticket.setIdComponente(dto.idComponente());
        ticket.setDescripcion(dto.descripcion());
        ticket.setEstado("ABIERTO");
        ticket.setFechaCreacion(LocalDateTime.now());

        return aResponseDTO(repo.save(ticket));
    }

    public TicketResponseDTO cerrarTicket(Long id) {
        TicketSoporte ticket = buscarEntidadPorId(id);
        if ("CERRADO".equals(ticket.getEstado())) {
            throw new EstadoInvalidoException("El ticket " + id + " ya está cerrado.");
        }
        ticket.setEstado("CERRADO");
        return aResponseDTO(repo.save(ticket));
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            throw new RecursoNoEncontradoException("El ticket con ID " + id + " no existe.");
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

    private void validarComponenteExiste(Long idComponente) {
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
