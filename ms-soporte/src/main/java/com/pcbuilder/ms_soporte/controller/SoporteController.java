package com.pcbuilder.ms_soporte.controller;

import com.pcbuilder.ms_soporte.dto.TicketRequestDTO;
import com.pcbuilder.ms_soporte.dto.TicketResponseDTO;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller REST de tickets de soporte, expuesto en el gateway bajo {@code /api/soporte/**}.
 * Delega toda la lógica de negocio en {@link SoporteService}; las respuestas se envuelven
 * en {@code EntityModel} con enlaces HATEOAS (self, listado).
 */
@RestController
@RequestMapping("/api/soporte")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Soporte Técnico", description = "Donde los usuarios reclaman por sus piezas")
public class SoporteController {

    private final SoporteService service;

    /** Lista todos los tickets de soporte existentes. */
    @Operation(summary = "Saca todos los reclamos")
    @GetMapping
    public ResponseEntity<List<EntityModel<TicketResponseDTO>>> listarTodos() {
        log.info("Sapeando todos los tickets de soporte");
        List<EntityModel<TicketResponseDTO>> tickets = service.listarTodos().stream()
                .map(ticket -> EntityModel.of(ticket,
                        linkTo(methodOn(SoporteController.class).buscarPorId(ticket.id())).withSelfRel(),
                        linkTo(methodOn(SoporteController.class).listarTodos()).withRel("todos-los-tickets")))
                .toList();
        return ResponseEntity.ok(tickets);
    }

    /** Busca un ticket por ID; delega en el service, que lanza RecursoNoEncontradoException (404) si no existe. */
    @Operation(summary = "Busca un ticket específico")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<TicketResponseDTO>> buscarPorId(@PathVariable Long id) {
        TicketResponseDTO ticket = service.buscarPorId(id);
        EntityModel<TicketResponseDTO> recurso = EntityModel.of(ticket,
                linkTo(methodOn(SoporteController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(SoporteController.class).listarTodos()).withRel("volver-a-tickets"));
        return ResponseEntity.ok(recurso);
    }

    /**
     * Crea un ticket nuevo. El service valida idUsuario/idComponente vía Feign contra
     * ms-usuarios y ms-componentes antes de persistir.
     */
    @Operation(summary = "Abre un nuevo reclamo (valida en ms-usuarios y ms-componentes)")
    @PostMapping
    public ResponseEntity<TicketResponseDTO> guardar(@Valid @RequestBody TicketRequestDTO dto) {
        log.info("El usuario ID: {} reclama por la pieza ID: {}", dto.idUsuario(), dto.idComponente());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    /** Cierra un ticket abierto; el service lanza EstadoInvalidoException (409) si ya estaba cerrado. */
    @Operation(summary = "Da por solucionado el ticket")
    @PutMapping("/{id}/cerrar")
    public ResponseEntity<TicketResponseDTO> cerrarTicket(@PathVariable Long id) {
        log.info("Cerrando el ticket ID: {}", id);
        return ResponseEntity.ok(service.cerrarTicket(id));
    }

    /**
     * Actualiza los datos de un ticket existente; revalida idUsuario/idComponente vía
     * Feign igual que en la creación.
     */
    @Operation(summary = "Edita los datos del ticket (descripción, usuario o componente asociado)")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<TicketResponseDTO>> actualizar(@PathVariable Long id,
                                                                      @Valid @RequestBody TicketRequestDTO dto) {
        log.info("Actualizando el ticket ID: {}", id);
        TicketResponseDTO ticket = service.actualizar(id, dto);
        EntityModel<TicketResponseDTO> recurso = EntityModel.of(ticket,
                linkTo(methodOn(SoporteController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(SoporteController.class).listarTodos()).withRel("volver-a-tickets"));
        return ResponseEntity.ok(recurso);
    }

    /** Elimina un ticket; el service lanza RecursoNoEncontradoException (404) si el ID no existe. */
    @Operation(summary = "Borra el ticket del sistema")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
