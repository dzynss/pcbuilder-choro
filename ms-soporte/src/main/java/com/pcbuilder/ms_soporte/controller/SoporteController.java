package com.pcbuilder.ms_soporte.controller;

import com.pcbuilder.ms_soporte.entity.TicketSoporte;
import com.pcbuilder.ms_soporte.service.SoporteService;
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
@RequestMapping("/api/soporte")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Soporte Técnico", description = "Donde los locos vienen a llorar por sus piezas")
public class SoporteController {

    private final SoporteService service;

    @Operation(summary = "Saca todos los reclamos")
    @GetMapping
    public ResponseEntity<List<EntityModel<TicketSoporte>>> listarTodos() {
        log.info("Sapeando todos los tickets de soporte");
        
        List<EntityModel<TicketSoporte>> ticketsConLinks = service.listarTodos().stream()
                .map(ticket -> EntityModel.of(ticket,
                        linkTo(methodOn(SoporteController.class).buscarPorId(ticket.getId())).withSelfRel(),
                        linkTo(methodOn(SoporteController.class).listarTodos()).withRel("todos-los-tickets")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ticketsConLinks);
    }

    @Operation(summary = "Busca un ticket específico")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<TicketSoporte>> buscarPorId(@PathVariable Long id) {
        TicketSoporte ticket = service.buscarPorId(id);
        
        EntityModel<TicketSoporte> recurso = EntityModel.of(ticket,
                linkTo(methodOn(SoporteController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(SoporteController.class).listarTodos()).withRel("volver-a-tickets"));
                
        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Abre un nuevo reclamo (Valida en Usuarios y Componentes)")
    @PostMapping
    public ResponseEntity<TicketSoporte> guardar(@Valid @RequestBody TicketSoporte ticket) {
        log.info("El loco ID: {} viene a dejar la cagá por la pieza ID: {}", ticket.getIdUsuario(), ticket.getIdComponente());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(ticket));
    }

    @Operation(summary = "Da por solucionado el atado")
    @PutMapping("/{id}/cerrar")
    public ResponseEntity<TicketSoporte> cerrarTicket(@PathVariable Long id) {
        log.info("Cerrando el ticket ID: {}", id);
        return ResponseEntity.ok(service.cerrarTicket(id));
    }

    @Operation(summary = "Borra el ticket del sistema")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}