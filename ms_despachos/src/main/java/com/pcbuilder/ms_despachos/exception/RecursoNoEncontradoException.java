package com.pcbuilder.ms_despachos.exception;

/**
 * Se lanza cuando un despacho o un usuario referenciado (vía Feign a ms-usuarios) no existe.
 * Capturada por GlobalExceptionHandler.handleNoEncontrado, responde 404 Not Found.
 */
public class RecursoNoEncontradoException extends RuntimeException {
    /** Construye la excepción con el mensaje de error a propagar en el body de la respuesta. */
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
