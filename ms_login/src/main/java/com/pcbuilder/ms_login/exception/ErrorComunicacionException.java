package com.pcbuilder.ms_login.exception;

/** Se lanza cuando ms-usuarios no responde o responde con error inesperado. */
public class ErrorComunicacionException extends RuntimeException {
    public ErrorComunicacionException(String mensaje) {
        super(mensaje);
    }
}
