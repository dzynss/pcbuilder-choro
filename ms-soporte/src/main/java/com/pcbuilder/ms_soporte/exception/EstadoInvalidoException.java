package com.pcbuilder.ms_soporte.exception;

/** Se lanza cuando se intenta una transición de estado inválida sobre un ticket. */
public class EstadoInvalidoException extends RuntimeException {
    public EstadoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
