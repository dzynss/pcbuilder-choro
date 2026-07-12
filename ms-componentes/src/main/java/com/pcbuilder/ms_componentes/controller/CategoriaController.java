package com.pcbuilder.ms_componentes.controller;

import com.pcbuilder.ms_componentes.dto.CategoriaRequestDTO;
import com.pcbuilder.ms_componentes.dto.CategoriaResponseDTO;
import com.pcbuilder.ms_componentes.service.CategoriaService;
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
 * Controller REST de categorías del catálogo (ej. "CPU", "RAM"), expuesto en
 * /api/categorias. Delega la lógica en {@link CategoriaService} y enriquece las
 * respuestas con enlaces HATEOAS.
 */
@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Categorías", description = "Categorías del catálogo de componentes")
public class CategoriaController {

    private final CategoriaService service;

    /** Lista todas las categorías, delegando en {@link CategoriaService#listarTodas()}. */
    @Operation(summary = "Lista todas las categorías del catálogo")
    @GetMapping
    public ResponseEntity<List<EntityModel<CategoriaResponseDTO>>> listar() {
        log.info("Los cabros están pidiendo todas las categorías");
        List<EntityModel<CategoriaResponseDTO>> categorias = service.listarTodas().stream()
                .map(c -> EntityModel.of(c,
                        linkTo(methodOn(CategoriaController.class).buscarUna(c.id())).withSelfRel(),
                        linkTo(methodOn(CategoriaController.class).listar()).withRel("todas-las-categorias")))
                .toList();
        return ResponseEntity.ok(categorias);
    }

    /** Busca una categoría por ID vía {@link CategoriaService#buscarPorId(Long)}; responde 404 si no existe (RecursoNoEncontradoException). */
    @Operation(summary = "Busca una categoría por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CategoriaResponseDTO>> buscarUna(@PathVariable Long id) {
        log.info("Buscando la categoría con ID: {}", id);
        CategoriaResponseDTO categoria = service.buscarPorId(id);
        EntityModel<CategoriaResponseDTO> recurso = EntityModel.of(categoria,
                linkTo(methodOn(CategoriaController.class).buscarUna(id)).withSelfRel(),
                linkTo(methodOn(CategoriaController.class).listar()).withRel("todas-las-categorias"));
        return ResponseEntity.ok(recurso);
    }

    /** Crea una categoría nueva; valida el DTO (@Valid) y delega en {@link CategoriaService#guardar(CategoriaRequestDTO)}; responde 201 Created. */
    @Operation(summary = "Agrega una nueva categoría al catálogo")
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> crear(@Valid @RequestBody CategoriaRequestDTO dto) {
        log.info("Chantando una nueva categoría a la BD: {}", dto.nombre());
        return new ResponseEntity<>(service.guardar(dto), HttpStatus.CREATED);
    }

    /** Actualiza una categoría existente; delega en {@link CategoriaService#actualizar(Long, CategoriaRequestDTO)}. */
    @Operation(summary = "Actualiza los datos de una categoría existente")
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequestDTO dto) {
        log.warn("Actualizando la categoría ID: {}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    /** Elimina una categoría por ID vía {@link CategoriaService#eliminar(Long)}; responde 204, o 409 si tiene componentes asociados. */
    @Operation(summary = "Elimina una categoría del catálogo")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        log.error("Eliminando la categoría ID: {}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
