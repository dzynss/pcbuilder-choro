package com.pcbuilder.ms_login.exception;

/**
 * Se lanza en {@link com.pcbuilder.ms_login.service.AuthService#login} cuando la llamada Feign
 * a ms-usuarios falla por un error distinto de credenciales inválidas (timeout, 5xx, etc.).
 * Capturada por {@link GlobalExceptionHandler#handleComunicacion} → HTTP 502 BAD_GATEWAY.
 */
public class ErrorComunicacionException extends RuntimeException {
    public ErrorComunicacionException(String mensaje) {
        super(mensaje);
    }
}
