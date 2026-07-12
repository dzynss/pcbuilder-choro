package com.pcbuilder.ms_ofertas.controller;

import com.pcbuilder.ms_ofertas.dto.OfertaRequestDTO;
import com.pcbuilder.ms_ofertas.dto.OfertaResponseDTO;
import com.pcbuilder.ms_ofertas.service.OfertaService;
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
 * Controlador REST de ofertas/cupones (/api/ofertas). Capa fina: delega toda la lógica en OfertaService
 * y envuelve las respuestas en EntityModel (HATEOAS) con enlaces a sí mismo y al listado completo.
 */
@RestController
@RequestMapping("/api/ofertas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Ofertas y Cupones", description = "Donde manejamos las rebajas del PC Builder")
public class OfertaController {

    private final OfertaService service;

    /** Lista todos los cupones registrados; delega en OfertaService.listarTodas(). */
    @Operation(summary = "Saca todos los cupones registrados")
    @GetMapping
    public ResponseEntity<List<EntityModel<OfertaResponseDTO>>> listarTodas() {
        log.info("Sapeando todas las ofertas");
        List<EntityModel<OfertaResponseDTO>> ofertas = service.listarTodas().stream()
                .map(oferta -> EntityModel.of(oferta,
                        linkTo(methodOn(OfertaController.class).buscarPorId(oferta.id())).withSelfRel(),
                        linkTo(methodOn(OfertaController.class).listarTodas()).withRel("todas-las-ofertas")))
                .toList();
        return ResponseEntity.ok(ofertas);
    }

    /** Busca un cupón por su ID interno; 404 (vía GlobalExceptionHandler) si no existe. */
    @Operation(summary = "Busca un cupón por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<OfertaResponseDTO>> buscarPorId(@PathVariable Long id) {
        OfertaResponseDTO oferta = service.buscarPorId(id);
        EntityModel<OfertaResponseDTO> recurso = EntityModel.of(oferta,
                linkTo(methodOn(OfertaController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(OfertaController.class).listarTodas()).withRel("volver-a-ofertas"));
        return ResponseEntity.ok(recurso);
    }

    /** Valida y busca un cupón por su código promocional (ej. al aplicar un cupón en una cotización). */
    @Operation(summary = "Valida un código promocional (Ej: PCGAMER2026)")
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<EntityModel<OfertaResponseDTO>> buscarPorCodigo(@PathVariable String codigo) {
        log.info("Revisando si el código {} es válido", codigo);
        OfertaResponseDTO oferta = service.buscarPorCodigo(codigo);
        EntityModel<OfertaResponseDTO> recurso = EntityModel.of(oferta,
                linkTo(methodOn(OfertaController.class).buscarPorCodigo(codigo)).withSelfRel());
        return ResponseEntity.ok(recurso);
    }

    /** Crea un nuevo cupón a partir del DTO validado; responde HTTP 201 (CREATED). */
    @Operation(summary = "Crea un cupón nuevo")
    @PostMapping
    public ResponseEntity<OfertaResponseDTO> guardar(@Valid @RequestBody OfertaRequestDTO dto) {
        log.info("Creando la oferta: {}", dto.codigo());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    /** Actualiza el porcentaje de descuento de un cupón existente (el código no es editable por esta vía). */
    @Operation(summary = "Actualiza el porcentaje de descuento de un cupón existente")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<OfertaResponseDTO>> actualizar(@PathVariable Long id,
                                                                      @Valid @RequestBody OfertaRequestDTO dto) {
        log.info("Actualizando la oferta con ID: {}", id);
        OfertaResponseDTO oferta = service.actualizar(id, dto);
        EntityModel<OfertaResponseDTO> recurso = EntityModel.of(oferta,
                linkTo(methodOn(OfertaController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(OfertaController.class).listarTodas()).withRel("volver-a-ofertas"));
        return ResponseEntity.ok(recurso);
    }

    /** Elimina un cupón por ID; responde HTTP 204 (NO_CONTENT). */
    @Operation(summary = "Borra un cupón que ya venció")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
