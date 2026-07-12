package com.pcbuilder.ms_despachos.controller;

import com.pcbuilder.ms_despachos.dto.DespachoRequestDTO;
import com.pcbuilder.ms_despachos.dto.DespachoResponseDTO;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller REST de despachos/envíos, expuesto en /api/despachos (a través del gateway en el path /api/despachos/**).
 * Capa delgada: delega toda la lógica de negocio en DespachoService y arma enlaces HATEOAS (EntityModel) sobre los DTO.
 */
@RestController
@RequestMapping("/api/despachos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Despachos y Encomiendas", description = "Donde vemos si la caja llega o se la roban")
public class DespachoController {

    private final DespachoService service;

    /** GET /api/despachos: lista todos los despachos, delega en DespachoService.listarTodos. */
    @Operation(summary = "Revisa todos los envíos que andan dando vuelta")
    @GetMapping
    public ResponseEntity<List<EntityModel<DespachoResponseDTO>>> listarTodos() {
        log.info("Sapeando el libro de despachos");
        List<EntityModel<DespachoResponseDTO>> envios = service.listarTodos().stream()
                .map(envio -> EntityModel.of(envio,
                        linkTo(methodOn(DespachoController.class).buscarPorId(envio.id())).withSelfRel(),
                        linkTo(methodOn(DespachoController.class).listarTodos()).withRel("todos-los-despachos")))
                .toList();
        return ResponseEntity.ok(envios);
    }

    /** GET /api/despachos/{id}: busca un despacho puntual; delega en DespachoService.buscarPorId (404 si no existe). */
    @Operation(summary = "Rastrea un envío por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<DespachoResponseDTO>> buscarPorId(@PathVariable Long id) {
        DespachoResponseDTO envio = service.buscarPorId(id);
        EntityModel<DespachoResponseDTO> recurso = EntityModel.of(envio,
                linkTo(methodOn(DespachoController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(DespachoController.class).listarTodos()).withRel("volver-a-despachos"));
        return ResponseEntity.ok(recurso);
    }

    /**
     * POST /api/despachos: crea un despacho nuevo. Delega en DespachoService.guardar, que valida
     * previamente (vía Feign a ms-usuarios) que el usuario destinatario exista.
     */
    @Operation(summary = "Registra una nueva encomienda (valida que el usuario exista)")
    @PostMapping
    public ResponseEntity<DespachoResponseDTO> guardar(@Valid @RequestBody DespachoRequestDTO dto) {
        log.info("Armando la encomienda pal usuario ID: {}", dto.idUsuario());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    /**
     * PATCH /api/despachos/{id}/estado: cambia el estado de seguimiento. Delega en
     * DespachoService.actualizarEstado, que valida el valor contra los estados permitidos.
     */
    @Operation(summary = "Le cambia el estado a la caja (ej: EN_RUTA)")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<DespachoResponseDTO> actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        log.info("Avisando que el paquete {} ahora está: {}", id, estado);
        return ResponseEntity.ok(service.actualizarEstado(id, estado));
    }

    /**
     * PUT /api/despachos/{id}: edita dirección/empresa de transporte. Delega en DespachoService.actualizar,
     * que también revalida al usuario contra ms-usuarios.
     */
    @Operation(summary = "Edita la dirección de envío o la empresa de transporte de la encomienda")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<DespachoResponseDTO>> actualizar(@PathVariable Long id,
                                                                        @Valid @RequestBody DespachoRequestDTO dto) {
        log.info("Actualizando el despacho ID: {}", id);
        DespachoResponseDTO envio = service.actualizar(id, dto);
        EntityModel<DespachoResponseDTO> recurso = EntityModel.of(envio,
                linkTo(methodOn(DespachoController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(DespachoController.class).listarTodos()).withRel("volver-a-despachos"));
        return ResponseEntity.ok(recurso);
    }

    /** DELETE /api/despachos/{id}: elimina el despacho; delega en DespachoService.eliminar (404 si no existe). */
    @Operation(summary = "Cancela el envío y borra el registro")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
