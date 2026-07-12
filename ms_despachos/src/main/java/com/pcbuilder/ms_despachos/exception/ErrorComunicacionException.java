package com.pcbuilder.ms_despachos.exception;

/**
 * Se lanza cuando falla la comunicación con ms-usuarios (FeignException distinta de 404).
 * Capturada por GlobalExceptionHandler.handleComunicacion, responde 502 Bad Gateway.
 */
public class ErrorComunicacionException extends RuntimeException {
    /** Construye la excepción con el mensaje de error a propagar en el body de la respuesta. */
    public ErrorComunicacionException(String mensaje) {
        super(mensaje);
    }
}
