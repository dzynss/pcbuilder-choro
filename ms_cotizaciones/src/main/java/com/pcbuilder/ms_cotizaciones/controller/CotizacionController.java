package com.pcbuilder.ms_cotizaciones.controller;

import com.pcbuilder.ms_cotizaciones.dto.CotizacionRequestDTO;
import com.pcbuilder.ms_cotizaciones.dto.CotizacionResponseDTO;
import com.pcbuilder.ms_cotizaciones.service.CotizacionService;
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
@RequestMapping("/api/cotizaciones")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cotizaciones", description = "Armado de cotizaciones validando usuario y componente en otros microservicios")
public class CotizacionController {

    private final CotizacionService service;

    @Operation(summary = "Busca todas las cotizaciones de un usuario")
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<EntityModel<CotizacionResponseDTO>>> buscarPorUsuario(@PathVariable Long idUsuario) {
        log.info("Buscando todas las cotizaciones del usuario ID: {}", idUsuario);
        List<EntityModel<CotizacionResponseDTO>> cotizaciones = service.buscarPorUsuario(idUsuario).stream()
                .map(c -> EntityModel.of(c,
                        linkTo(methodOn(CotizacionController.class).buscarUno(c.id())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(cotizaciones);
    }

    @Operation(summary = "Calcula el total gastado por un usuario en todas sus cotizaciones")
    @GetMapping("/usuario/{idUsuario}/total")
    public ResponseEntity<EntityModel<Double>> totalPorUsuario(@PathVariable Long idUsuario) {
        log.info("Calculando el total gastado por el usuario ID: {}", idUsuario);
        EntityModel<Double> recurso = EntityModel.of(service.calcularTotalPorUsuario(idUsuario),
                linkTo(methodOn(CotizacionController.class).totalPorUsuario(idUsuario)).withSelfRel(),
                linkTo(methodOn(CotizacionController.class).buscarPorUsuario(idUsuario)).withRel("cotizaciones-del-usuario"));
        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Lista todas las cotizaciones")
    @GetMapping
    public ResponseEntity<List<EntityModel<CotizacionResponseDTO>>> listar() {
        List<EntityModel<CotizacionResponseDTO>> cotizaciones = service.buscarTodos().stream()
                .map(c -> EntityModel.of(c,
                        linkTo(methodOn(CotizacionController.class).buscarUno(c.id())).withSelfRel(),
                        linkTo(methodOn(CotizacionController.class).listar()).withRel("todas-las-cotizaciones")))
                .toList();
        return ResponseEntity.ok(cotizaciones);
    }

    @Operation(summary = "Busca una cotización por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CotizacionResponseDTO>> buscarUno(@PathVariable Long id) {
        CotizacionResponseDTO cotizacion = service.buscarPorId(id);
        EntityModel<CotizacionResponseDTO> recurso = EntityModel.of(cotizacion,
                linkTo(methodOn(CotizacionController.class).buscarUno(id)).withSelfRel(),
                linkTo(methodOn(CotizacionController.class).listar()).withRel("todas-las-cotizaciones"));
        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Crea una cotización validando usuario y componente, y calculando el total")
    @PostMapping
    public ResponseEntity<CotizacionResponseDTO> crear(@Valid @RequestBody CotizacionRequestDTO dto) {
        log.info("Armando una nueva cotización pal usuario {} con la pieza {}", dto.idUsuario(), dto.idComponente());
        return new ResponseEntity<>(service.guardar(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualiza una cotización existente")
    @PutMapping("/{id}")
    public ResponseEntity<CotizacionResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody CotizacionRequestDTO dto) {
        log.info("Actualizando la cotización ID: {}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @Operation(summary = "Elimina una cotización")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        log.info("Eliminando la cotización ID: {}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
