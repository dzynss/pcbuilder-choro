package com.pcbuilder.ms_resenas.controller;

import com.pcbuilder.ms_resenas.dto.ResenaRequestDTO;
import com.pcbuilder.ms_resenas.dto.ResenaResponseDTO;
import com.pcbuilder.ms_resenas.service.ResenaService;
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
 * Controller REST de reseñas, expuesto en /api/resenas (a través del gateway en /api/resenas/**).
 * Capa fina: delega toda la lógica de negocio en {@link ResenaService} y arma respuestas HATEOAS con {@code EntityModel}.
 */
@RestController
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reseñas", description = "Comentarios y calificaciones de los componentes")
public class ResenaController {

    private final ResenaService service;

    /** Lista todas las reseñas, delegando en {@code service.buscarTodos()}. */
    @Operation(summary = "Lista todas las reseñas")
    @GetMapping
    public ResponseEntity<List<EntityModel<ResenaResponseDTO>>> listar() {
        log.info("Tasando todas las reseñas");
        List<EntityModel<ResenaResponseDTO>> resenas = service.buscarTodos().stream()
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(ResenaController.class).buscarUno(r.id())).withSelfRel(),
                        linkTo(methodOn(ResenaController.class).listar()).withRel("todas-las-resenas")))
                .toList();
        return ResponseEntity.ok(resenas);
    }

    /** Busca una reseña por ID vía {@code service.buscarPorId}; si no existe, propaga RecursoNoEncontradoException (404). */
    @Operation(summary = "Busca una reseña por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ResenaResponseDTO>> buscarUno(@PathVariable Long id) {
        ResenaResponseDTO resena = service.buscarPorId(id);
        EntityModel<ResenaResponseDTO> recurso = EntityModel.of(resena,
                linkTo(methodOn(ResenaController.class).buscarUno(id)).withSelfRel(),
                linkTo(methodOn(ResenaController.class).listar()).withRel("todas-las-resenas"));
        return ResponseEntity.ok(recurso);
    }

    /** Filtra reseñas por calificación (1 a 5 estrellas), delegando en {@code service.buscarPorEstrellas}. */
    @Operation(summary = "Busca reseñas por cantidad de estrellas")
    @GetMapping("/estrellas/{calificacion}")
    public ResponseEntity<List<EntityModel<ResenaResponseDTO>>> buscarPorCalificacion(@PathVariable Integer calificacion) {
        log.info("Buscando las reseñas que tengan {} estrellas", calificacion);
        List<EntityModel<ResenaResponseDTO>> resenas = service.buscarPorEstrellas(calificacion).stream()
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(ResenaController.class).buscarUno(r.id())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(resenas);
    }

    /**
     * Crea una reseña; {@code service.guardar} valida vía Feign ({@code ComponenteClient}) que el componente
     * exista en ms-componentes antes de persistir. Retorna 201 Created.
     */
    @Operation(summary = "Crea una nueva reseña (valida que el componente exista en ms-componentes)")
    @PostMapping
    public ResponseEntity<ResenaResponseDTO> crear(@Valid @RequestBody ResenaRequestDTO dto) {
        log.info("El autor {} quiere dejar un comentario pal componente {}", dto.autor(), dto.idComponente());
        return new ResponseEntity<>(service.guardar(dto), HttpStatus.CREATED);
    }

    /** Actualiza una reseña existente; también revalida el componente contra ms-componentes vía {@code service.actualizar}. */
    @Operation(summary = "Actualiza una reseña existente")
    @PutMapping("/{id}")
    public ResponseEntity<ResenaResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ResenaRequestDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    /** Elimina una reseña por ID; delega en {@code service.eliminar} y retorna 204 No Content. */
    @Operation(summary = "Elimina una reseña")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        log.warn("Borrando la reseña ID: {}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
