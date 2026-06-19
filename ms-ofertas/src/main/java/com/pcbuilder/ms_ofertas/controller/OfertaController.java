package com.pcbuilder.ms_ofertas.controller;

import com.pcbuilder.ms_ofertas.entity.Oferta;
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
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/ofertas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Ofertas y Cupones", description = "Donde manejamos las rebajas del PC Builder")
public class OfertaController {

    private final OfertaService service;

    @Operation(summary = "Saca todos los cupones registrados")
    @GetMapping
    public ResponseEntity<List<EntityModel<Oferta>>> listarTodas() {
        log.info("Sapeando todas las ofertas");
        
        List<EntityModel<Oferta>> ofertasConLinks = service.listarTodas().stream()
                .map(oferta -> EntityModel.of(oferta,
                        linkTo(methodOn(OfertaController.class).buscarPorId(oferta.getId())).withSelfRel(),
                        linkTo(methodOn(OfertaController.class).listarTodas()).withRel("todas-las-ofertas")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ofertasConLinks);
    }

    @Operation(summary = "Busca un cupón por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Oferta>> buscarPorId(@PathVariable Long id) {
        Oferta oferta = service.buscarPorId(id);
        
        EntityModel<Oferta> recurso = EntityModel.of(oferta,
                linkTo(methodOn(OfertaController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(OfertaController.class).listarTodas()).withRel("volver-a-ofertas"));
                
        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Valida un código promocional (Ej: PCGAMER2026)")
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<EntityModel<Oferta>> buscarPorCodigo(@PathVariable String codigo) {
        log.info("Revisando si el código {} es de vio", codigo);
        Oferta oferta = service.buscarPorCodigo(codigo);
        
        EntityModel<Oferta> recurso = EntityModel.of(oferta,
                linkTo(methodOn(OfertaController.class).buscarPorCodigo(codigo)).withSelfRel());
                
        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Crea un cupón nuevecito")
    @PostMapping
    public ResponseEntity<Oferta> guardar(@Valid @RequestBody Oferta oferta) {
        log.info("Creando la mansa oferta: {}", oferta.getCodigo());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(oferta));
    }

    @Operation(summary = "Borra un cupón que ya cagó")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}