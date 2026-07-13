package com.pcbuilder.ms_login.exception;

/**
 * Se lanza en {@link com.pcbuilder.ms_login.service.AuthService#registrar} cuando
 * ms-usuarios rechaza la creación del usuario (correo ya registrado o datos inválidos).
 * Capturada por {@link GlobalExceptionHandler#handleRegistroInvalido} → HTTP 409 CONFLICT.
 */
public class RegistroInvalidoException extends RuntimeException {
    public RegistroInvalidoException(String mensaje) {
        super(mensaje);
    }
}
