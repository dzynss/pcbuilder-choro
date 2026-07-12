package com.pcbuilder.ms_usuarios.controller;

import com.pcbuilder.ms_usuarios.dto.LoginRequestDTO;
import com.pcbuilder.ms_usuarios.dto.UsuarioRequestDTO;
import com.pcbuilder.ms_usuarios.dto.UsuarioResponseDTO;
import com.pcbuilder.ms_usuarios.service.UsuarioService;
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
 * Controller REST de usuarios: expone CRUD y validación de credenciales bajo
 * /api/usuarios. Es una capa delgada que delega toda la lógica en
 * {@link UsuarioService}; las respuestas se envuelven con enlaces HATEOAS
 * (self / todos-los-usuarios). Detrás del gateway, este microservicio corre
 * en el puerto 8083.
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usuarios", description = "Registro, consulta y autenticación de usuarios del PC Builder")
public class UsuarioController {

    private final UsuarioService service;

    /**
     * Valida correo/password contra la BD delegando en {@link UsuarioService#login}
     * y retorna los datos del usuario (sin password). Lanza 401 vía
     * GlobalExceptionHandler si las credenciales son inválidas.
     */
    @Operation(summary = "Valida las credenciales y devuelve los datos del usuario (sin password)")
    @PostMapping("/login")
    public ResponseEntity<UsuarioResponseDTO> iniciarSesion(@Valid @RequestBody LoginRequestDTO credenciales) {
        log.info("El usuario con correo {} está intentando entrar al sistema", credenciales.correo());
        UsuarioResponseDTO usuarioLogueado = service.login(credenciales);
        log.info("Usuario {} autenticado correctamente", usuarioLogueado.nombre());
        return ResponseEntity.ok(usuarioLogueado);
    }

    /** Lista todos los usuarios delegando en {@link UsuarioService#buscarTodos()}, cada uno envuelto en enlaces HATEOAS. */
    @Operation(summary = "Lista todos los usuarios registrados")
    @GetMapping
    public ResponseEntity<List<EntityModel<UsuarioResponseDTO>>> listar() {
        List<EntityModel<UsuarioResponseDTO>> usuarios = service.buscarTodos().stream()
                .map(u -> EntityModel.of(u,
                        linkTo(methodOn(UsuarioController.class).buscarUno(u.id())).withSelfRel(),
                        linkTo(methodOn(UsuarioController.class).listar()).withRel("todos-los-usuarios")))
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Busca un usuario por ID delegando en {@link UsuarioService#buscarPorId}.
     * Este endpoint es consultado vía Feign por otros microservicios
     * (ms_login, ms_cotizaciones, ms-soporte, ms_despachos, ms_notificaciones)
     * para validar que un usuario referenciado exista.
     */
    @Operation(summary = "Busca un usuario por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioResponseDTO>> buscarUno(@PathVariable Long id) {
        UsuarioResponseDTO usuario = service.buscarPorId(id);
        EntityModel<UsuarioResponseDTO> recurso = EntityModel.of(usuario,
                linkTo(methodOn(UsuarioController.class).buscarUno(id)).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).listar()).withRel("todos-los-usuarios"));
        return ResponseEntity.ok(recurso);
    }

    /**
     * Registra un usuario nuevo delegando en {@link UsuarioService#guardar}.
     * Es la única ruta POST (junto con /login) que el gateway deja pasar sin
     * JWT, ya que es cómo se crea la cuenta inicial.
     */
    @Operation(summary = "Registra un usuario nuevo")
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioRequestDTO dto) {
        return new ResponseEntity<>(service.guardar(dto), HttpStatus.CREATED);
    }

    /** Actualiza un usuario existente delegando en {@link UsuarioService#actualizar}; 404 si el ID no existe. */
    @Operation(summary = "Actualiza los datos de un usuario existente")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    /** Elimina un usuario por ID delegando en {@link UsuarioService#eliminar}; 404 si el ID no existe. */
    @Operation(summary = "Elimina un usuario por su ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
