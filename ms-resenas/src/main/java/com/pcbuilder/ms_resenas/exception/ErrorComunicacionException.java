package com.pcbuilder.ms_resenas.exception;

/**
 * Se lanza cuando falla la comunicación con ms-componentes vía Feign (remoto caído, timeout, error 5xx, etc),
 * distinto de un simple "no encontrado". Capturada por {@code GlobalExceptionHandler#handleComunicacion},
 * responde HTTP 502 (Bad Gateway).
 */
public class ErrorComunicacionException extends RuntimeException {
    public ErrorComunicacionException(String mensaje) {
        super(mensaje);
    }
}
