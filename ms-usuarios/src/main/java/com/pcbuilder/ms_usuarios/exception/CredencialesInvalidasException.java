package com.pcbuilder.ms_usuarios.exception;

/**
 * Se lanza desde {@link com.pcbuilder.ms_usuarios.service.UsuarioService#login} cuando
 * el correo/password no coinciden. Capturada por {@link GlobalExceptionHandler}, que
 * la traduce a HTTP 401 (UNAUTHORIZED).
 */
public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
