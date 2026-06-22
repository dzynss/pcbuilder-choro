package com.pcbuilder.ms_notificaciones.controller;

import com.pcbuilder.ms_notificaciones.entity.Notificacion;
import com.pcbuilder.ms_notificaciones.service.NotificacionService;
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
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notificaciones", description = "El cartero del PC Builder")
public class NotificacionController {

    private final NotificacionService service;

    @Operation(summary = "Saca todos los correos y mensajes enviados")
    @GetMapping
    public ResponseEntity<List<EntityModel<Notificacion>>> listarTodas() {
        log.info("Sapeando la bandeja de salida");
        
        List<EntityModel<Notificacion>> notisConLinks = service.listarTodas().stream()
                .map(noti -> EntityModel.of(noti,
                        linkTo(methodOn(NotificacionController.class).buscarPorId(noti.getId())).withSelfRel(),
                        linkTo(methodOn(NotificacionController.class).listarTodas()).withRel("todas-las-notificaciones")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(notisConLinks);
    }

    @Operation(summary = "Revisa un mensaje por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Notificacion>> buscarPorId(@PathVariable Long id) {
        Notificacion noti = service.buscarPorId(id);
        
        EntityModel<Notificacion> recurso = EntityModel.of(noti,
                linkTo(methodOn(NotificacionController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(NotificacionController.class).listarTodas()).withRel("volver-a-bandeja"));
                
        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Manda un correo o SMS nuevo")
    @PostMapping
    public ResponseEntity<Notificacion> guardar(@Valid @RequestBody Notificacion noti) {
        log.info("Mandándole el medio spam al loco ID: {}", noti.getIdUsuario());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(noti));
    }

    @Operation(summary = "Borra un mensaje del registro")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}