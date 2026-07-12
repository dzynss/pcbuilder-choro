package com.pcbuilder.ms_soporte.exception;

/**
 * Se lanza cuando se intenta una transición de estado inválida sobre un ticket
 * (ej. cerrar un ticket ya cerrado). Capturada por {@link GlobalExceptionHandler},
 * responde HTTP 409 (Conflict).
 */
public class EstadoInvalidoException extends RuntimeException {
    public EstadoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
