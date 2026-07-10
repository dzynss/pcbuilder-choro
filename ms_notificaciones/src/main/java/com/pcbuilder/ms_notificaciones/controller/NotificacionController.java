package com.pcbuilder.ms_notificaciones.controller;

import com.pcbuilder.ms_notificaciones.dto.NotificacionRequestDTO;
import com.pcbuilder.ms_notificaciones.dto.NotificacionResponseDTO;
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
    public ResponseEntity<List<EntityModel<NotificacionResponseDTO>>> listarTodas() {
        log.info("Sapeando la bandeja de salida");
        List<EntityModel<NotificacionResponseDTO>> notis = service.listarTodas().stream()
                .map(noti -> EntityModel.of(noti,
                        linkTo(methodOn(NotificacionController.class).buscarPorId(noti.id())).withSelfRel(),
                        linkTo(methodOn(NotificacionController.class).listarTodas()).withRel("todas-las-notificaciones")))
                .toList();
        return ResponseEntity.ok(notis);
    }

    @Operation(summary = "Revisa un mensaje por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<NotificacionResponseDTO>> buscarPorId(@PathVariable Long id) {
        NotificacionResponseDTO noti = service.buscarPorId(id);
        EntityModel<NotificacionResponseDTO> recurso = EntityModel.of(noti,
                linkTo(methodOn(NotificacionController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(NotificacionController.class).listarTodas()).withRel("volver-a-bandeja"));
        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Manda un correo o SMS nuevo (valida que el usuario exista)")
    @PostMapping
    public ResponseEntity<NotificacionResponseDTO> guardar(@Valid @RequestBody NotificacionRequestDTO dto) {
        log.info("Mandándole el mensaje al usuario ID: {}", dto.idUsuario());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    @Operation(summary = "Edita el tipo de mensaje o el contenido de una notificación")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<NotificacionResponseDTO>> actualizar(@PathVariable Long id,
                                                                            @Valid @RequestBody NotificacionRequestDTO dto) {
        log.info("Actualizando la notificación ID: {}", id);
        NotificacionResponseDTO noti = service.actualizar(id, dto);
        EntityModel<NotificacionResponseDTO> recurso = EntityModel.of(noti,
                linkTo(methodOn(NotificacionController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(NotificacionController.class).listarTodas()).withRel("volver-a-bandeja"));
        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Borra un mensaje del registro")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
