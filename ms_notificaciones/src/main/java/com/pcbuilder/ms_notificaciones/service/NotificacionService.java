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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionService {

    private final NotificacionRepository repo;
    private final UsuarioClient usuarioClient;

    public List<NotificacionResponseDTO> listarTodas() {
        log.info("Listando todas las notificaciones");
        return repo.findAll().stream().map(this::aResponseDTO).toList();
    }

    public NotificacionResponseDTO buscarPorId(Long id) {
        log.info("Buscando la notificación con ID: {}", id);
        return aResponseDTO(buscarEntidadPorId(id));
    }

    public NotificacionResponseDTO guardar(NotificacionRequestDTO dto) {
        log.info("Guardando una notificación nueva para el usuario ID: {}", dto.idUsuario());
        validarUsuarioExiste(dto.idUsuario());

        Notificacion noti = new Notificacion();
        noti.setIdUsuario(dto.idUsuario());
        noti.setTipoMensaje(dto.tipoMensaje());
        noti.setContenido(dto.contenido());
        noti.setEstado("ENVIADO");
        noti.setFechaEnvio(LocalDateTime.now());

        Notificacion guardada = repo.save(noti);
        log.info("Notificación guardada con ID: {}", guardada.getId());
        return aResponseDTO(guardada);
    }

    public NotificacionResponseDTO actualizar(Long id, NotificacionRequestDTO dto) {
        log.info("Actualizando la notificación con ID: {}", id);
        Notificacion noti = buscarEntidadPorId(id);

        noti.setTipoMensaje(dto.tipoMensaje());
        noti.setContenido(dto.contenido());

        Notificacion actualizada = repo.save(noti);
        log.info("Notificación con ID: {} actualizada", actualizada.getId());
        return aResponseDTO(actualizada);
    }

    public void eliminar(Long id) {
        log.info("Eliminando la notificación con ID: {}", id);
        if (!repo.existsById(id)) {
            throw new RecursoNoEncontradoException("La notificación con ID " + id + " no existe.");
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

    private Notificacion buscarEntidadPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La notificación con ID " + id + " no existe."));
    }

    private NotificacionResponseDTO aResponseDTO(Notificacion n) {
        return new NotificacionResponseDTO(n.getId(), n.getIdUsuario(), n.getTipoMensaje(),
                n.getContenido(), n.getEstado(), n.getFechaEnvio());
    }
}
