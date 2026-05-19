package com.pcbuilder.ms_resenas.service;

import com.pcbuilder.ms_resenas.entity.Resena;
import com.pcbuilder.ms_resenas.repository.ResenaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class ResenaService {
    
    private final ResenaRepository repo;
    private final WebClient webClient;

    // inyectamos el repo y preparamos el WebClient pa llamar al ms-componentes (ESTA PARTE LA HICIMOS CON AYUDA NO CACHAMOS NADA DEL WEB CLIENT)
    public ResenaService(ResenaRepository repo, WebClient.Builder webClientBuilder) {
        this.repo = repo;
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081/api/componentes").build();
    }

    public List<Resena> buscarTodos() { return repo.findAll(); }
    public Resena buscarPorId(Long id) { return repo.findById(id).orElseThrow(); }
    public void eliminar(Long id) { repo.deleteById(id); }
    
    // busqueda (requisito de la rubrica)
    public List<Resena> buscarPorEstrellas(Integer calificacion) {
        return repo.findByCalificacion(calificacion);
    }

    // guardado (wow, no dice para nada que es un metodo para guardar.)
    public Resena guardar(Resena resena) {
        try {
            // segun lo que nos dijo Gemini, aqui se espera la llamada (?)
            webClient.get()
                    .uri("/{id}", resena.getIdComponente())
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
            
            // verificacion si se guarda
            return repo.save(resena);
        } catch (Exception e) {
            // por si tira error
            throw new RuntimeException("Tai' weno! Ese componente no existe, no podi reseñarlo.");
        }
    }
}