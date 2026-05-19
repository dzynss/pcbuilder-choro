package com.pcbuilder.ms_componentes.controller;
import com.pcbuilder.ms_componentes.entity.Componente;
import com.pcbuilder.ms_componentes.service.ComponenteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/componentes")
@RequiredArgsConstructor
@Slf4j
public class ComponenteController {

    private final ComponenteService service;

    @GetMapping
    public ResponseEntity<List<Componente>> listar() {
        log.info("Los cabros están pidiendo todos los componentes");
        return ResponseEntity.ok(service.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Componente> buscarUno(@PathVariable Long id) {
        log.info("Buscando la pieza con ID: {}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Componente> crear(@RequestBody Componente c) {
        log.info("Chantando un nuevo componente a la BD: {}", c.getNombre());
        return new ResponseEntity<>(service.guardar(c), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Componente> actualizar(@PathVariable Long id, @RequestBody Componente c) {
        log.warn("Actualizando la pieza ID: {}", id);
        Componente existe = service.buscarPorId(id);
        existe.setNombre(c.getNombre());
        existe.setMarca(c.getMarca());
        existe.setPrecio(c.getPrecio());
        existe.setStock(c.getStock());
        existe.setCategoria(c.getCategoria());
        return ResponseEntity.ok(service.guardar(existe));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        log.error("Piteándose el componente ID: {}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}