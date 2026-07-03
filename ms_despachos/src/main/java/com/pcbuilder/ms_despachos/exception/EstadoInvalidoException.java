package com.pcbuilder.ms_despachos.exception;

/** Se lanza cuando se intenta fijar un estado de seguimiento que no existe en el dominio. */
public class EstadoInvalidoException extends RuntimeException {
    public EstadoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
