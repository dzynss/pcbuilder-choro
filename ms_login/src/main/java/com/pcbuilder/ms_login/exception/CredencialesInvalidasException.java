package com.pcbuilder.ms_login.exception;

/**
 * Se lanza en {@link com.pcbuilder.ms_login.service.AuthService#login} cuando ms-usuarios
 * responde 401/404 al validar las credenciales (correo o contraseña incorrectos).
 * Capturada por {@link GlobalExceptionHandler#handleCredenciales} → HTTP 401 UNAUTHORIZED.
 */
public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
