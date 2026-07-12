package com.pcbuilder.ms_inventario.exception;

/**
 * Excepción de negocio lanzada cuando no se encuentra un registro de inventario solicitado.
 * Es capturada por GlobalExceptionHandler, que la traduce a una respuesta HTTP 404 (NOT_FOUND).
 */
public class RecursoNoEncontradoException extends RuntimeException {
    /** Crea la excepción con el mensaje que se devolverá en el cuerpo de la respuesta de error. */
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
