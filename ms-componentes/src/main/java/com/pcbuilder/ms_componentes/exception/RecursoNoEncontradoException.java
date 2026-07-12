package com.pcbuilder.ms_componentes.exception;

/**
 * Excepción de negocio lanzada cuando un recurso (componente o categoría) no
 * existe por el ID solicitado. Es capturada por {@link GlobalExceptionHandler},
 * que la traduce a una respuesta HTTP 404 Not Found con cuerpo JSON estándar.
 */
public class RecursoNoEncontradoException extends RuntimeException {
    /** Crea la excepción con el mensaje que se devolverá en el cuerpo de la respuesta 404. */
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
