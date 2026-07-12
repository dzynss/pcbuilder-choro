package com.pcbuilder.ms_notificaciones.exception;

/**
 * Se lanza cuando la notificación o el usuario referenciado no existen (ej. usuario no
 * encontrado en ms-usuarios vía Feign). Capturada por {@code GlobalExceptionHandler} → HTTP 404.
 */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
