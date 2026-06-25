package com.pcbuilder.ms_soporte.exception;

/** Se lanza cuando falla la comunicacion con ms-usuarios o ms-componentes. */
public class ErrorComunicacionException extends RuntimeException {
    public ErrorComunicacionException(String mensaje) {
        super(mensaje);
    }
}
