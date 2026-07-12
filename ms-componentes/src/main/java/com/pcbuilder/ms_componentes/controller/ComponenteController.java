package com.pcbuilder.ms_componentes.controller;

import com.pcbuilder.ms_componentes.dto.ComponenteRequestDTO;
import com.pcbuilder.ms_componentes.dto.ComponenteResponseDTO;
import com.pcbuilder.ms_componentes.service.ComponenteService;
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
 * Controller REST del catálogo de componentes (piezas de PC), expuesto en
 * /api/componentes y enrutado por el gateway bajo el mismo prefijo. Delega toda
 * la lógica en {@link ComponenteService} y enriquece las respuestas con enlaces
 * HATEOAS. Este catálogo es la fuente de precio/stock que ms_cotizaciones,
 * ms-resenas y ms-soporte consumen vía Feign a través de ComponenteResponseDTO.
 */
@RestController
@RequestMapping("/api/componentes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Componentes", description = "Catálogo de piezas de PC")
public class ComponenteController {

    private final ComponenteService service;

    /** Lista todos los componentes del catálogo, delegando en {@link ComponenteService#buscarTodos()}. */
    @Operation(summary = "Lista todos los componentes del catálogo")
    @GetMapping
    public ResponseEntity<List<EntityModel<ComponenteResponseDTO>>> listar() {
        log.info("Los cabros están pidiendo todos los componentes");
        List<EntityModel<ComponenteResponseDTO>> componentes = service.buscarTodos().stream()
                .map(c -> EntityModel.of(c,
                        linkTo(methodOn(ComponenteController.class).buscarUno(c.id())).withSelfRel(),
                        linkTo(methodOn(ComponenteController.class).listar()).withRel("todos-los-componentes")))
                .toList();
        return ResponseEntity.ok(componentes);
    }

    /** Busca un componente por ID vía {@link ComponenteService#buscarPorId(Long)}; responde 404 si no existe (RecursoNoEncontradoException). */
    @Operation(summary = "Busca un componente por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ComponenteResponseDTO>> buscarUno(@PathVariable Long id) {
        log.info("Buscando la pieza con ID: {}", id);
        ComponenteResponseDTO componente = service.buscarPorId(id);
        EntityModel<ComponenteResponseDTO> recurso = EntityModel.of(componente,
                linkTo(methodOn(ComponenteController.class).buscarUno(id)).withSelfRel(),
                linkTo(methodOn(ComponenteController.class).listar()).withRel("todos-los-componentes"));
        return ResponseEntity.ok(recurso);
    }

    /** Crea un componente nuevo; valida el DTO (@Valid) y delega en {@link ComponenteService#guardar(ComponenteRequestDTO)}; responde 201 Created. */
    @Operation(summary = "Agrega un nuevo componente al catálogo")
    @PostMapping
    public ResponseEntity<ComponenteResponseDTO> crear(@Valid @RequestBody ComponenteRequestDTO dto) {
        log.info("Chantando un nuevo componente a la BD: {}", dto.nombre());
        return new ResponseEntity<>(service.guardar(dto), HttpStatus.CREATED);
    }

    /** Actualiza un componente existente; delega en {@link ComponenteService#actualizar(Long, ComponenteRequestDTO)}. */
    @Operation(summary = "Actualiza los datos de un componente existente")
    @PutMapping("/{id}")
    public ResponseEntity<ComponenteResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ComponenteRequestDTO dto) {
        log.warn("Actualizando la pieza ID: {}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    /** Elimina un componente por ID vía {@link ComponenteService#eliminar(Long)}; responde 204 No Content. */
    @Operation(summary = "Elimina un componente del catálogo")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        log.error("Eliminando el componente ID: {}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
