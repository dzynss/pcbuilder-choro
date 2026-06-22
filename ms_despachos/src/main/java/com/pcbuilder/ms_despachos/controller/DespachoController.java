package com.pcbuilder.ms_despachos.controller;

import com.pcbuilder.ms_despachos.entity.Despacho;
import com.pcbuilder.ms_despachos.service.DespachoService;
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
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/despachos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Despachos y Encomiendas", description = "Donde vemos si la caja llega o se la roban")
public class DespachoController {

    private final DespachoService service;

    @Operation(summary = "Revisa todos los envíos que andan dando vuelta")
    @GetMapping
    public ResponseEntity<List<EntityModel<Despacho>>> listarTodos() {
        log.info("Sapeando el libro de despachos");
        
        List<EntityModel<Despacho>> enviosConLinks = service.listarTodos().stream()
                .map(envio -> EntityModel.of(envio,
                        linkTo(methodOn(DespachoController.class).buscarPorId(envio.getId())).withSelfRel(),
                        linkTo(methodOn(DespachoController.class).listarTodos()).withRel("todos-los-despachos")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(enviosConLinks);
    }

    @Operation(summary = "Rastrea un envío por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Despacho>> buscarPorId(@PathVariable Long id) {
        Despacho envio = service.buscarPorId(id);
        
        EntityModel<Despacho> recurso = EntityModel.of(envio,
                linkTo(methodOn(DespachoController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(DespachoController.class).listarTodos()).withRel("volver-a-despachos"));
                
        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Registra una nueva caja pa' mandar")
    @PostMapping
    public ResponseEntity<Despacho> guardar(@Valid @RequestBody Despacho despacho) {
        log.info("Armando la encomienda pal loco ID: {}", despacho.getIdUsuario());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(despacho));
    }

    @Operation(summary = "Le cambia el estado a la caja (ej: EN_RUTA)")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Despacho> actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        log.info("Avisando que el paquete {} ahora está: {}", id, estado);
        return ResponseEntity.ok(service.actualizarEstado(id, estado));
    }

    @Operation(summary = "Cancela el envío y borra el registro")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}