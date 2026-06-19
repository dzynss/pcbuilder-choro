package com.pcbuilder.ms_soporte.service;

import com.pcbuilder.ms_soporte.entity.TicketSoporte;
import com.pcbuilder.ms_soporte.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SoporteService {

    private final TicketRepository repo;
    private final RestTemplate restTemplate;

    public SoporteService(TicketRepository repo) {
        this.repo = repo;
        this.restTemplate = new RestTemplate();
    }

    public List<TicketSoporte> listarTodos() { return repo.findAll(); }

    public TicketSoporte buscarPorId(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Ese ticket no existe, compare."));
    }

    public TicketSoporte guardar(TicketSoporte ticket) {
        // 1. Verificamos si el loco existe (Pegándole al puerto 8083 de ms-usuarios)
        try {
            restTemplate.getForObject("http://localhost:8083/api/usuarios/" + ticket.getIdUsuario(), Object.class);
        } catch (Exception e) {
            throw new RuntimeException("¡Vo' soy vio! Ese usuario no existe en la base de datos.");
        }

        // 2. Verificamos si la pieza existe (Pegándole al puerto 8085 de ms-componentes)
        try {
            restTemplate.getForObject("http://localhost:8085/api/componentes/" + ticket.getIdComponente(), Object.class);
        } catch (Exception e) {
            throw new RuntimeException("Media pifia. Esa pieza no la vendemos nosotros.");
        }

        // Si todo sale bacán, preparamos el ticket y lo guardamos
        ticket.setEstado("ABIERTO");
        ticket.setFechaCreacion(LocalDateTime.now());
        return repo.save(ticket);
    }
    
    public TicketSoporte cerrarTicket(Long id) {
        TicketSoporte ticket = buscarPorId(id);
        ticket.setEstado("CERRADO");
        return repo.save(ticket);
    }

    public void eliminar(Long id) { repo.deleteById(id); }
}