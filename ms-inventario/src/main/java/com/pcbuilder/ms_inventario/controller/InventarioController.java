package com.pcbuilder.ms_inventario.controller;

import com.pcbuilder.ms_inventario.dto.InventarioRequestDTO;
import com.pcbuilder.ms_inventario.dto.InventarioResponseDTO;
import com.pcbuilder.ms_inventario.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controlador REST de inventario (/api/inventario). Capa fina: delega toda la lógica en InventarioService
 * y envuelve las respuestas en EntityModel (HATEOAS) con enlaces a sí mismo y al listado completo.
 */
@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Inventario", description = "La API pa' manejar el stock de las piezas del PC Builder")
public class InventarioController {

    private final InventarioService service;

    /** Lista todos los registros de inventario; delega en InventarioService.listarTodos(). */
    @Operation(summary = "Saca todos los registros de la bodega")
    @GetMapping
    public ResponseEntity<List<EntityModel<InventarioResponseDTO>>> listarTodos() {
        log.info("Listando toda la merca del inventario");
        List<EntityModel<InventarioResponseDTO>> inventario = service.listarTodos().stream()
                .map(inv -> EntityModel.of(inv,
                        linkTo(methodOn(InventarioController.class).buscarPorId(inv.id())).withSelfRel(),
                        linkTo(methodOn(InventarioController.class).listarTodos()).withRel("inventario-completo")))
                .toList();
        return ResponseEntity.ok(inventario);
    }

    /** Busca un registro de inventario por su ID interno; 404 (vía GlobalExceptionHandler) si no existe. */
    @Operation(summary = "Busca el stock de una pieza por su ID interno")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<InventarioResponseDTO>> buscarPorId(@PathVariable Long id) {
        log.info("Buscando el registro de inventario con ID: {}", id);
        InventarioResponseDTO inv = service.buscarPorId(id);
        EntityModel<InventarioResponseDTO> recurso = EntityModel.of(inv,
                linkTo(methodOn(InventarioController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(InventarioController.class).listarTodos()).withRel("volver-al-inventario"));
        return ResponseEntity.ok(recurso);
    }

    /** Crea un nuevo registro de inventario a partir del DTO validado; responde HTTP 201 (CREATED). */
    @Operation(summary = "Agrega nueva merca a la bodega")
    @PostMapping
    public ResponseEntity<InventarioResponseDTO> guardar(@Valid @RequestBody InventarioRequestDTO dto) {
        log.info("Guardando nueva merca pal componente ID: {}", dto.idComponente());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    /** Actualiza cantidad y ubicación de un registro de inventario existente identificado por ID. */
    @Operation(summary = "Actualiza el stock de un registro de inventario")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<InventarioResponseDTO>> actualizar(@PathVariable Long id,
                                                                          @Valid @RequestBody InventarioRequestDTO dto) {
        log.info("Actualizando el registro de inventario con ID: {}", id);
        InventarioResponseDTO inv = service.actualizar(id, dto);
        EntityModel<InventarioResponseDTO> recurso = EntityModel.of(inv,
                linkTo(methodOn(InventarioController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(InventarioController.class).listarTodos()).withRel("volver-al-inventario"));
        return ResponseEntity.ok(recurso);
    }

    /** Elimina un registro de inventario por ID; responde HTTP 204 (NO_CONTENT). */
    @Operation(summary = "Borra un registro si se quemó la bodega")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.warn("Borrando el registro de inventario ID: {}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
