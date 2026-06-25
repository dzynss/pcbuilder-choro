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

@RestController
@RequestMapping("/api/componentes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Componentes", description = "Catálogo de piezas de PC")
public class ComponenteController {

    private final ComponenteService service;

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

    @Operation(summary = "Agrega un nuevo componente al catálogo")
    @PostMapping
    public ResponseEntity<ComponenteResponseDTO> crear(@Valid @RequestBody ComponenteRequestDTO dto) {
        log.info("Chantando un nuevo componente a la BD: {}", dto.nombre());
        return new ResponseEntity<>(service.guardar(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualiza los datos de un componente existente")
    @PutMapping("/{id}")
    public ResponseEntity<ComponenteResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ComponenteRequestDTO dto) {
        log.warn("Actualizando la pieza ID: {}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @Operation(summary = "Elimina un componente del catálogo")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        log.error("Eliminando el componente ID: {}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
