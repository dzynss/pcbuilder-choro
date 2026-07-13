package com.pcbuilder.ms_login.controller;

import com.pcbuilder.ms_login.dto.HistorialResponseDTO;
import com.pcbuilder.ms_login.dto.LoginRequestDTO;
import com.pcbuilder.ms_login.dto.RegistroRequestDTO;
import com.pcbuilder.ms_login.dto.TokenJwtResponseDTO;
import com.pcbuilder.ms_login.dto.TokenResponseDTO;
import com.pcbuilder.ms_login.service.AuthService;
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
 * Controlador REST de autenticación (base /api/auth). Capa fina que delega toda la
 * lógica en {@link AuthService}; este servicio no valida usuarios localmente, solo
 * expone el login (que internamente llama a ms-usuarios vía Feign) y el historial de intentos.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticación", description = "Login y emisión de tokens JWT, validando credenciales en ms-usuarios")
public class AuthController {

    private final AuthService service;

    /**
     * POST /api/auth/login: delega en {@link AuthService#login} para validar credenciales
     * contra ms-usuarios y devuelve un JWT. Errores de credenciales/comunicación son
     * manejados por GlobalExceptionHandler (401/502 respectivamente).
     */
    @Operation(summary = "Valida credenciales contra ms-usuarios y devuelve un token JWT")
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody LoginRequestDTO credenciales) {
        log.info("El usuario con correo {} quiere su Token JWT", credenciales.correo());
        TokenResponseDTO token = service.login(credenciales);
        log.info("Token generado correctamente pa' {}", credenciales.correo());
        return ResponseEntity.ok(token);
    }

    /**
     * POST /api/auth/register: delega en {@link AuthService#registrar} para crear el
     * usuario en ms-usuarios y devuelve un JWT (auto-login tras registro). Errores de
     * correo duplicado/datos inválidos o de comunicación son manejados por
     * GlobalExceptionHandler (409/502 respectivamente).
     */
    @Operation(summary = "Registra un usuario nuevo en ms-usuarios y devuelve un token JWT")
    @PostMapping("/register")
    public ResponseEntity<TokenResponseDTO> registrar(@Valid @RequestBody RegistroRequestDTO datos) {
        log.info("Registro solicitado para el correo {}", datos.correo());
        TokenResponseDTO token = service.registrar(datos);
        log.info("Registro y token generados correctamente pa' {}", datos.correo());
        return new ResponseEntity<>(token, HttpStatus.CREATED);
    }

    /**
     * GET /api/auth/historial: delega en {@link AuthService#listarHistorial} y envuelve
     * cada resultado en un EntityModel HATEOAS con su enlace propio (self).
     */
    @Operation(summary = "Lista el historial de intentos de login (éxitos y fallos)")
    @GetMapping("/historial")
    public ResponseEntity<List<EntityModel<HistorialResponseDTO>>> historial() {
        List<EntityModel<HistorialResponseDTO>> historial = service.listarHistorial().stream()
                .map(h -> EntityModel.of(h, linkTo(methodOn(AuthController.class).historial()).withSelfRel()))
                .toList();
        return ResponseEntity.ok(historial);
    }

    /**
     * GET /api/auth/tokens: delega en {@link AuthService#listarTokens} y envuelve
     * cada resultado en un EntityModel HATEOAS con su enlace propio (self).
     */
    @Operation(summary = "Lista los tokens JWT registrados tras cada login exitoso")
    @GetMapping("/tokens")
    public ResponseEntity<List<EntityModel<TokenJwtResponseDTO>>> tokens() {
        List<EntityModel<TokenJwtResponseDTO>> tokens = service.listarTokens().stream()
                .map(t -> EntityModel.of(t, linkTo(methodOn(AuthController.class).tokens()).withSelfRel()))
                .toList();
        return ResponseEntity.ok(tokens);
    }
}
