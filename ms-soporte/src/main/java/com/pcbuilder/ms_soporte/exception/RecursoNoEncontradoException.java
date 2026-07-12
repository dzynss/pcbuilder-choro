package com.pcbuilder.ms_soporte.exception;

/**
 * Se lanza cuando un ticket, usuario o componente referenciado no existe. Capturada
 * por {@link GlobalExceptionHandler}, responde HTTP 404 (Not Found).
 */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
