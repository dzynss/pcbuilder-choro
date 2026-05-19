package com.pcbuilder.ms_resenas.controller;

import com.pcbuilder.ms_resenas.entity.Resena;
import com.pcbuilder.ms_resenas.service.ResenaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
@Slf4j
public class ResenaController {

    private final ResenaService service;

    @GetMapping
    public ResponseEntity<List<Resena>> listar() {
        log.info("Tasando todas las reseñas");
        return ResponseEntity.ok(service.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resena> buscarUno(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // busqueda por atributo distinto (estrellas)
    @GetMapping("/estrellas/{calificacion}")
    public ResponseEntity<List<Resena>> buscarPorCalificacion(@PathVariable Integer calificacion) {
        log.info("Buscando las reseñas que tengan {} estrellas", calificacion);
        return ResponseEntity.ok(service.buscarPorEstrellas(calificacion));
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Resena r) {
        log.info("El loco {} quiere dejar un comentario pal componente {}", r.getAutor(), r.getIdComponente());
        try {
            return new ResponseEntity<>(service.guardar(r), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Resena> actualizar(@PathVariable Long id, @RequestBody Resena r) {
        Resena existe = service.buscarPorId(id);
        existe.setAutor(r.getAutor());
        existe.setComentario(r.getComentario());
        existe.setCalificacion(r.getCalificacion());
        existe.setIdComponente(r.getIdComponente());
        return ResponseEntity.ok(service.guardar(existe)); // volvera a validar con el otro ms
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        log.warn("Borrando la reseña ID: {}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}