package com.pcbuilder.ms_soporte.exception;

/**
 * Se lanza cuando falla la comunicación con ms-usuarios o ms-componentes vía Feign
 * (distinto de un 404). Capturada por {@link GlobalExceptionHandler}, responde HTTP 502 (Bad Gateway).
 */
public class ErrorComunicacionException extends RuntimeException {
    public ErrorComunicacionException(String mensaje) {
        super(mensaje);
    }
}
