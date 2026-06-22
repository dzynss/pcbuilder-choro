package com.pcbuilder.ms_notificaciones.service;

import com.pcbuilder.ms_notificaciones.entity.Notificacion;
import com.pcbuilder.ms_notificaciones.repository.NotificacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacionService {

    private final NotificacionRepository repo;
    private final RestTemplate restTemplate;

    public NotificacionService(NotificacionRepository repo) {
        this.repo = repo;
        this.restTemplate = new RestTemplate();
    }

    public List<Notificacion> listarTodas() { return repo.findAll(); }

    public Notificacion buscarPorId(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Ese mensaje no existe, hermano."));
    }

    public Notificacion guardar(Notificacion noti) {
        // Le preguntamos al ms-usuarios (Puerto 8083) si el loco existe
        try {
            restTemplate.getForObject("http://localhost:8083/api/usuarios/" + noti.getIdUsuario(), Object.class);
        } catch (Exception e) {
            // Manejamos el condoro remoto pa' que no explote la app [cite: 41, 57]
            throw new RuntimeException("¡Atajando pifia! Ese usuario no está en los registros.");
        }

        noti.setEstado("ENVIADO");
        noti.setFechaEnvio(LocalDateTime.now());
        return repo.save(noti);
    }

    public void eliminar(Long id) { repo.deleteById(id); }
}