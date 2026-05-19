package com.pcbuilder.ms_login.controller;

import com.pcbuilder.ms_login.entity.HistorialLogin;
import com.pcbuilder.ms_login.repository.HistorialRepository;
import com.pcbuilder.ms_login.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final HistorialRepository repo;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String correo = credenciales.get("correo");
        log.info("El loco con correo {} quiere su Token JWT", correo);

        HistorialLogin historial = new HistorialLogin();
        historial.setCorreoUsuario(correo);
        historial.setFechaHora(LocalDateTime.now());

        try {
            // Le pegamos el grito al ms-usuarios (Puerto 8082)
            ResponseEntity<Object> respuestaUsuarios = restTemplate.postForEntity(
                    "http://localhost:8082/api/usuarios/login", 
                    credenciales, 
                    Object.class
            );

            if (respuestaUsuarios.getStatusCode() == HttpStatus.OK) {
                String tokenGenerado = jwtUtil.generarToken(correo);
                
                historial.setEstado("EXITOSO");
                repo.save(historial);
                
                log.info("Token generado pulento pa' {}", correo);
                return ResponseEntity.ok(Map.of("token", tokenGenerado));
            }

        } catch (Exception e) {
            log.error("El loco metió mal el dedo o la clave es charcha: {}", e.getMessage());
        }

        historial.setEstado("FALLIDO");
        repo.save(historial);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("¡Saca pa' allá! Credenciales mulas.");
    }
}