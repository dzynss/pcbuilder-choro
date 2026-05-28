package com.pcbuilder.ms_login.controller;

import com.pcbuilder.ms_login.entity.HistorialLogin;
import com.pcbuilder.ms_login.repository.HistorialRepository;
import com.pcbuilder.ms_login.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
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
            // ARREGLO PULENTO: Blindamos la petición pa' que sea JSON puro
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(credenciales, headers);

            // Le pegamos el grito al ms-usuarios (Puerto 8083)
            ResponseEntity<Object> respuestaUsuarios = restTemplate.postForEntity(
                    "http://localhost:8083/api/usuarios/login",
                    requestEntity,
                    Object.class
            );

            // Si el bodeguero dice que todo está OK, soltamos la pulsera VIP (el token)
            if (respuestaUsuarios.getStatusCode() == HttpStatus.OK) {
                String tokenGenerado = jwtUtil.generarToken(correo);

                historial.setEstado("EXITOSO");
                repo.save(historial);

                log.info("Token generado pulento pa' {}", correo);
                return ResponseEntity.ok(Map.of("token", tokenGenerado));
            }

        } catch (HttpClientErrorException e) {
            // Si el ms-usuarios tira error (como un 401), lo atajamos acá sin que explote la app
            log.error("El ms-usuarios nos cerró la puerta en la cara. Código: {}", e.getStatusCode());
        } catch (Exception e) {
            // Por si el puerto 8082 está apagao o se cae la conexión
            log.error("Medio condoro de conexión con el ms-usuarios: {}", e.getMessage());
        }

        // Si la weá llegó hasta acá abajo, es porque rebotó brígido
        historial.setEstado("FALLIDO");
        repo.save(historial);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("¡Saca pa' allá! Credenciales mulas o el servidor de usuarios está apagao.");
    }
}