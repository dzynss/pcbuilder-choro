package com.pcbuilder.ms_ofertas.exception;

/**
 * Excepción de negocio lanzada cuando no se encuentra la oferta/cupón solicitado.
 * Es capturada por GlobalExceptionHandler, que la traduce a una respuesta HTTP 404 (NOT_FOUND).
 */
public class RecursoNoEncontradoException extends RuntimeException {
    /** Crea la excepción con el mensaje que se devolverá en el cuerpo de la respuesta de error. */
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
