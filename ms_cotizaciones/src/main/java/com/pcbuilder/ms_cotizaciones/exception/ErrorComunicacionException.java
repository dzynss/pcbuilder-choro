package com.pcbuilder.ms_cotizaciones.exception;

/** Se lanza cuando falla la comunicacion con otro microservicio (remoto caido, timeout, etc). */
public class ErrorComunicacionException extends RuntimeException {
    public ErrorComunicacionException(String mensaje) {
        super(mensaje);
    }
}
