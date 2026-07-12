package com.pcbuilder.ms_despachos.exception;

/**
 * Se lanza cuando se intenta fijar un estado de seguimiento que no existe en el dominio (fuera de ESTADOS_VALIDOS).
 * Capturada por GlobalExceptionHandler.handleEstadoInvalido, responde 400 Bad Request.
 */
public class EstadoInvalidoException extends RuntimeException {
    /** Construye la excepción con el mensaje de error a propagar en el body de la respuesta. */
    public EstadoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
