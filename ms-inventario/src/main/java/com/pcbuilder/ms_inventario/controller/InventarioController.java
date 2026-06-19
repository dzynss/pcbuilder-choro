package com.pcbuilder.ms_inventario.controller;

import com.pcbuilder.ms_inventario.entity.Inventario;
import com.pcbuilder.ms_inventario.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Inventario", description = "La API pa' manejar el stock de las piezas del PC Builder")
public class InventarioController {

    private final InventarioService service;

    @Operation(summary = "Saca todos los registros de la bodega")
    @GetMapping
    public ResponseEntity<List<EntityModel<Inventario>>> listarTodos() {
        log.info("Listando toda la merca del inventario");
        
        // Magia del HATEOAS: Le pegamos el link a cada elemento de la lista
        List<EntityModel<Inventario>> inventarioConLinks = service.listarTodos().stream()
                .map(inv -> EntityModel.of(inv,
                        linkTo(methodOn(InventarioController.class).buscarPorId(inv.getId())).withSelfRel(),
                        linkTo(methodOn(InventarioController.class).listarTodos()).withRel("inventario-completo")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(inventarioConLinks);
    }

    @Operation(summary = "Busca el stock de una pieza por su ID interno")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Inventario>> buscarPorId(@PathVariable Long id) {
        log.info("Buscando el registro de inventario con ID: {}", id);
        Inventario inv = service.buscarPorId(id);
        
        // HATEOAS pa' un solo registro
        EntityModel<Inventario> recurso = EntityModel.of(inv,
                linkTo(methodOn(InventarioController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(InventarioController.class).listarTodos()).withRel("volver-al-inventario"));
                
        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Agrega nueva merca a la bodega")
    @PostMapping
    public ResponseEntity<Inventario> guardar(@Valid @RequestBody Inventario inventario) {
        log.info("Guardando nueva merca pal componente ID: {}", inventario.getIdComponente());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(inventario));
    }

    @Operation(summary = "Borra un registro si se quemó la bodega")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.warn("Borrando el registro de inventario ID: {}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}