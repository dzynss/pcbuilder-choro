package com.pcbuilder.ms_usuarios.exception;

/** Se lanza cuando el cuerpo de la petición es sintácticamente válido pero viola una regla de negocio. */
public class SolicitudInvalidaException extends RuntimeException {
    public SolicitudInvalidaException(String mensaje) {
        super(mensaje);
    }
}
