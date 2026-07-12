package com.pcbuilder.ms_notificaciones.exception;

/**
 * Se lanza cuando falla la comunicación con ms-usuarios vía Feign (timeout, error 5xx, etc.).
 * Capturada por {@code GlobalExceptionHandler} → HTTP 502 (Bad Gateway).
 */
public class ErrorComunicacionException extends RuntimeException {
    public ErrorComunicacionException(String mensaje) {
        super(mensaje);
    }
}
