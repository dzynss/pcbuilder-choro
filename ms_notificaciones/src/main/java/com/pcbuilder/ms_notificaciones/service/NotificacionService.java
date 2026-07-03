package com.pcbuilder.ms_notificaciones.service;

import com.pcbuilder.ms_notificaciones.client.UsuarioClient;
import com.pcbuilder.ms_notificaciones.dto.NotificacionRequestDTO;
import com.pcbuilder.ms_notificaciones.dto.NotificacionResponseDTO;
import com.pcbuilder.ms_notificaciones.entity.Notificacion;
import com.pcbuilder.ms_notificaciones.exception.ErrorComunicacionException;
import com.pcbuilder.ms_notificaciones.exception.RecursoNoEncontradoException;
import com.pcbuilder.ms_notificaciones.repository.NotificacionRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository repo;
    private final UsuarioClient usuarioClient;

    public List<NotificacionResponseDTO> listarTodas() {
        return repo.findAll().stream().map(this::aResponseDTO).toList();
    }

    public NotificacionResponseDTO buscarPorId(Long id) {
        return aResponseDTO(buscarEntidadPorId(id));
    }

    public NotificacionResponseDTO guardar(NotificacionRequestDTO dto) {
        validarUsuarioExiste(dto.idUsuario());

        Notificacion noti = new Notificacion();
        noti.setIdUsuario(dto.idUsuario());
        noti.setTipoMensaje(dto.tipoMensaje());
        noti.setContenido(dto.contenido());
        noti.setEstado("ENVIADO");
        noti.setFechaEnvio(LocalDateTime.now());

        return aResponseDTO(repo.save(noti));
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            throw new RecursoNoEncontradoException("La notificación con ID " + id + " no existe.");
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

    private Notificacion buscarEntidadPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La notificación con ID " + id + " no existe."));
    }

    private NotificacionResponseDTO aResponseDTO(Notificacion n) {
        return new NotificacionResponseDTO(n.getId(), n.getIdUsuario(), n.getTipoMensaje(),
                n.getContenido(), n.getEstado(), n.getFechaEnvio());
    }
}
