package com.pcbuilder.ms_usuarios.exception;

/**
 * Se lanza cuando el cuerpo de la petición es sintácticamente válido pero viola una
 * regla de negocio (p.ej. {@link com.pcbuilder.ms_usuarios.service.UsuarioService#guardar}
 * exige password no vacío al crear). Capturada por {@link GlobalExceptionHandler}, que
 * la traduce a HTTP 400 (BAD_REQUEST).
 */
public class SolicitudInvalidaException extends RuntimeException {
    public SolicitudInvalidaException(String mensaje) {
        super(mensaje);
    }
}
