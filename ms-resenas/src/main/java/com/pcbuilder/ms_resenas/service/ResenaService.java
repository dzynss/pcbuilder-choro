package com.pcbuilder.ms_resenas.service;

import com.pcbuilder.ms_resenas.entity.Resena;
import com.pcbuilder.ms_resenas.repository.ResenaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ResenaService {

    private final ResenaRepository repo;
    private final RestTemplate restTemplate;

    // Inyectamos el repo y armamos el teléfono normal (RestTemplate)
    public ResenaService(ResenaRepository repo) {
        this.repo = repo;
        this.restTemplate = new RestTemplate();
    }

    public List<Resena> buscarTodos() { return repo.findAll(); }
    public Resena buscarPorId(Long id) { return repo.findById(id).orElseThrow(); }
    public void eliminar(Long id) { repo.deleteById(id); }

    public List<Resena> buscarPorEstrellas(Integer calificacion) {
        return repo.findByCalificacion(calificacion);
    }

    // EL GUARDADO USANDO RESTTEMPLATE PA' SALVAR LA PLATA
    public Resena guardar(Resena resena) {
        try {
            // Le pegamos el grito al ms-componentes. 
            // OJO: Asumiendo que Componentes sigue corriendo en el puerto 8081
            String url = "http://localhost:8085/api/componentes/" + resena.getIdComponente();

            restTemplate.getForObject(url, Object.class);

            // Si el componente existe, guardamos la reseña
            return repo.save(resena);
        } catch (Exception e) {
            // Si nos tiran la foca o no contesta, atajamos el condoro
            throw new RuntimeException("¡Vo' soy vio! Ese componente no existe o el servidor está apagao.");
        }
    }
} 