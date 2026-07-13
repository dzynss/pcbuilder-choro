package com.pcbuilder.ms_login.service;

import com.pcbuilder.ms_login.client.UsuarioClient;
import com.pcbuilder.ms_login.dto.HistorialResponseDTO;
import com.pcbuilder.ms_login.dto.LoginRequestDTO;
import com.pcbuilder.ms_login.dto.RegistroRequestDTO;
import com.pcbuilder.ms_login.dto.TokenJwtResponseDTO;
import com.pcbuilder.ms_login.dto.TokenResponseDTO;
import com.pcbuilder.ms_login.dto.UsuarioResponseDTO;
import com.pcbuilder.ms_login.entity.HistorialLogin;
import com.pcbuilder.ms_login.entity.TokenJwt;
import com.pcbuilder.ms_login.exception.CredencialesInvalidasException;
import com.pcbuilder.ms_login.exception.ErrorComunicacionException;
import com.pcbuilder.ms_login.exception.RegistroInvalidoException;
import com.pcbuilder.ms_login.repository.HistorialRepository;
import com.pcbuilder.ms_login.repository.TokenJwtRepository;
import com.pcbuilder.ms_login.util.JwtUtil;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Lógica de negocio del login. No tiene tabla propia de usuarios: delega la validación
 * de credenciales en ms-usuarios vía {@link UsuarioClient} (Feign) y solo persiste el
 * registro de cada intento en {@link HistorialRepository} (tabla historial_logins).
 * Usado por {@link com.pcbuilder.ms_login.controller.AuthController}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final HistorialRepository repo;
    private final TokenJwtRepository tokenRepo;
    private final JwtUtil jwtUtil;
    private final UsuarioClient usuarioClient;

    /**
     * Valida las credenciales contra ms-usuarios (Feign) y, si son correctas, emite un JWT
     * vía {@link JwtUtil#generarToken}. Siempre registra el intento (EXITOSO/FALLIDO) en
     * {@link HistorialRepository}. Un 401/404 de ms-usuarios se traduce en
     * {@link com.pcbuilder.ms_login.exception.CredencialesInvalidasException} (401);
     * cualquier otro fallo Feign, en {@link com.pcbuilder.ms_login.exception.ErrorComunicacionException} (502).
     */
    public TokenResponseDTO login(LoginRequestDTO credenciales) {
        log.info("Intento de login para el correo: {}", credenciales.correo());
        HistorialLogin historial = new HistorialLogin();
        historial.setCorreoUsuario(credenciales.correo());
        historial.setFechaHora(LocalDateTime.now());

        try {
            usuarioClient.login(credenciales);
        } catch (FeignException.Unauthorized | FeignException.NotFound e) {
            log.warn("Credenciales inválidas para el correo: {}", credenciales.correo());
            historial.setEstado("FALLIDO");
            repo.save(historial);
            throw new CredencialesInvalidasException("Correo o clave incorrectos.");
        } catch (FeignException e) {
            log.error("Error de comunicación con ms-usuarios al validar el correo {}: {}", credenciales.correo(), e.getMessage());
            historial.setEstado("FALLIDO");
            repo.save(historial);
            throw new ErrorComunicacionException("ms-usuarios no respondió correctamente: " + e.getMessage());
        }

        String token = jwtUtil.generarToken(credenciales.correo());
        historial.setEstado("EXITOSO");
        repo.save(historial);
        registrarToken(credenciales.correo(), token);
        log.info("Login exitoso para el correo: {}", credenciales.correo());
        return new TokenResponseDTO(token);
    }

    /**
     * Registra un usuario nuevo delegando en ms-usuarios (Feign) y, si se crea con éxito,
     * emite un JWT igual que {@link #login}. Un correo duplicado o datos inválidos en
     * ms-usuarios se traducen en {@link RegistroInvalidoException} (409); cualquier otro
     * fallo Feign, en {@link ErrorComunicacionException} (502).
     */
    public TokenResponseDTO registrar(RegistroRequestDTO datos) {
        log.info("Intento de registro para el correo: {}", datos.correo());

        UsuarioResponseDTO usuarioCreado;
        try {
            usuarioCreado = usuarioClient.registrar(datos);
        } catch (FeignException.Conflict e) {
            log.warn("Correo ya registrado: {}", datos.correo());
            throw new RegistroInvalidoException("El correo ya está registrado.");
        } catch (FeignException.BadRequest e) {
            log.warn("Datos de registro inválidos para {}: {}", datos.correo(), e.getMessage());
            throw new RegistroInvalidoException("Datos de registro inválidos.");
        } catch (FeignException e) {
            log.error("Error de comunicación con ms-usuarios al registrar el correo {}: {}", datos.correo(), e.getMessage());
            throw new ErrorComunicacionException("ms-usuarios no respondió correctamente: " + e.getMessage());
        }

        String token = jwtUtil.generarToken(usuarioCreado.correo());
        registrarToken(usuarioCreado.correo(), token);
        log.info("Registro exitoso, token emitido para: {}", usuarioCreado.correo());
        return new TokenResponseDTO(token);
    }

    /**
     * Persiste el JWT recién emitido en {@link TokenJwtRepository} (tabla tokens_jwt), junto con
     * su fecha de emisión y de expiración (esta última extraída del propio token vía
     * {@link JwtUtil#obtenerExpiracion}). Complementa el registro de {@link HistorialLogin}:
     * ese registra el intento, este registra el token efectivamente entregado.
     */
    private void registrarToken(String correo, String token) {
        TokenJwt registro = new TokenJwt();
        registro.setCorreoUsuario(correo);
        registro.setToken(token);
        registro.setFechaEmision(LocalDateTime.now());
        registro.setFechaExpiracion(jwtUtil.obtenerExpiracion(token));
        tokenRepo.save(registro);
    }

    /** Lista todos los intentos de login registrados, mapeados a {@link HistorialResponseDTO}. */
    public List<HistorialResponseDTO> listarHistorial() {
        log.info("Listando historial de logins");
        return repo.findAll().stream()
                .map(h -> new HistorialResponseDTO(h.getId(), h.getCorreoUsuario(), h.getFechaHora(), h.getEstado()))
                .toList();
    }

    /** Lista todos los JWT registrados, mapeados a {@link TokenJwtResponseDTO}. */
    public List<TokenJwtResponseDTO> listarTokens() {
        log.info("Listando tokens JWT registrados");
        return tokenRepo.findAll().stream()
                .map(t -> new TokenJwtResponseDTO(t.getId(), t.getCorreoUsuario(), t.getToken(), t.getFechaEmision(), t.getFechaExpiracion()))
                .toList();
    }
}
