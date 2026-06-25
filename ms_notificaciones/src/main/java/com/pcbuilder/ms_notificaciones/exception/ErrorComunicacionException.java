package com.pcbuilder.ms_notificaciones.exception;

/** Se lanza cuando falla la comunicacion con ms-usuarios. */
public class ErrorComunicacionException extends RuntimeException {
    public ErrorComunicacionException(String mensaje) {
        super(mensaje);
    }
}
