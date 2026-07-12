package com.pcbuilder.ms_cotizaciones.exception;

/**
 * Se lanza cuando falla la comunicación con ms-usuarios o ms-componentes vía Feign
 * (remoto caído, timeout, error inesperado; no aplica cuando el recurso simplemente no existe).
 * Capturada por {@code GlobalExceptionHandler}, que responde HTTP 502 Bad Gateway.
 */
public class ErrorComunicacionException extends RuntimeException {
    public ErrorComunicacionException(String mensaje) {
        super(mensaje);
    }
}
