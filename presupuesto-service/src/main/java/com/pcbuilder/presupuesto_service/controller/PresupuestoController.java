package com.pcbuilder.presupuesto_service.controller;

import com.pcbuilder.presupuesto_service.dto.PresupuestoRequestDTO;
import com.pcbuilder.presupuesto_service.dto.PresupuestoResponseDTO;
import com.pcbuilder.presupuesto_service.service.PresupuestoService;
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

@RestController
@RequestMapping("/api/presupuestos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Presupuestos", description = "Administra los presupuestos del PC Builder")
public class PresupuestoController {

    private final PresupuestoService service;

    @Operation(summary = "Lista todos los presupuestos registrados")
    @GetMapping
    public ResponseEntity<List<EntityModel<PresupuestoResponseDTO>>> listarTodos() {
        log.info("Listando todos los presupuestos");
        List<EntityModel<PresupuestoResponseDTO>> presupuestos = service.listarTodos().stream()
                .map(p -> EntityModel.of(p,
                        linkTo(methodOn(PresupuestoController.class).listarTodos()).withSelfRel()))
                .toList();
        return ResponseEntity.ok(presupuestos);
    }

    @Operation(summary = "Crea un presupuesto nuevo")
    @PostMapping
    public ResponseEntity<PresupuestoResponseDTO> guardar(@Valid @RequestBody PresupuestoRequestDTO dto) {
        log.info("Creando presupuesto con estado: {}", dto.estado());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }
}
