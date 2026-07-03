package com.pcbuilder.ms_login.service;

import com.pcbuilder.ms_login.client.UsuarioClient;
import com.pcbuilder.ms_login.dto.HistorialResponseDTO;
import com.pcbuilder.ms_login.dto.LoginRequestDTO;
import com.pcbuilder.ms_login.dto.TokenResponseDTO;
import com.pcbuilder.ms_login.entity.HistorialLogin;
import com.pcbuilder.ms_login.exception.CredencialesInvalidasException;
import com.pcbuilder.ms_login.exception.ErrorComunicacionException;
import com.pcbuilder.ms_login.repository.HistorialRepository;
import com.pcbuilder.ms_login.util.JwtUtil;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final HistorialRepository repo;
    private final JwtUtil jwtUtil;
    private final UsuarioClient usuarioClient;

    public TokenResponseDTO login(LoginRequestDTO credenciales) {
        HistorialLogin historial = new HistorialLogin();
        historial.setCorreoUsuario(credenciales.correo());
        historial.setFechaHora(LocalDateTime.now());

        try {
            usuarioClient.login(credenciales);
        } catch (FeignException.Unauthorized | FeignException.NotFound e) {
            historial.setEstado("FALLIDO");
            repo.save(historial);
            throw new CredencialesInvalidasException("Correo o clave incorrectos.");
        } catch (FeignException e) {
            historial.setEstado("FALLIDO");
            repo.save(historial);
            throw new ErrorComunicacionException("ms-usuarios no respondió correctamente: " + e.getMessage());
        }

        String token = jwtUtil.generarToken(credenciales.correo());
        historial.setEstado("EXITOSO");
        repo.save(historial);
        return new TokenResponseDTO(token);
    }

    public List<HistorialResponseDTO> listarHistorial() {
        return repo.findAll().stream()
                .map(h -> new HistorialResponseDTO(h.getId(), h.getCorreoUsuario(), h.getFechaHora(), h.getEstado()))
                .toList();
    }
}
