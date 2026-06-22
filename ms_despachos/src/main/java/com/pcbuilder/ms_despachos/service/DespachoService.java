package com.pcbuilder.ms_despachos.service;

import com.pcbuilder.ms_despachos.entity.Despacho;
import com.pcbuilder.ms_despachos.repository.DespachoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DespachoService {

    private final DespachoRepository repo;
    private final RestTemplate restTemplate;

    public DespachoService(DespachoRepository repo) {
        this.repo = repo;
        this.restTemplate = new RestTemplate();
    }

    public List<Despacho> listarTodos() { return repo.findAll(); }

    public Despacho buscarPorId(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("No pillamos ese número de seguimiento."));
    }

    public Despacho guardar(Despacho despacho) {
        // Le pegamos un telefonazo al ms-usuarios (Puerto 8083) pa' ver si el cliente es de verdad
        try {
            restTemplate.getForObject("http://localhost:8083/api/usuarios/" + despacho.getIdUsuario(), Object.class);
        } catch (Exception e) {
            throw new RuntimeException("¡Pifia! Ese usuario no existe, ¿a quién chucha le estay mandando la caja?");
        }

        despacho.setEstadoSeguimiento("BODEGA");
        despacho.setFechaDespacho(LocalDateTime.now());
        return repo.save(despacho);
    }

    public Despacho actualizarEstado(Long id, String nuevoEstado) {
        Despacho despacho = buscarPorId(id);
        despacho.setEstadoSeguimiento(nuevoEstado);
        return repo.save(despacho);
    }

    public void eliminar(Long id) { repo.deleteById(id); }
}