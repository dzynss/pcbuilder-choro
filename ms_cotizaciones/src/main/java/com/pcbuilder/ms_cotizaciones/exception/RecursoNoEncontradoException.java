package com.pcbuilder.ms_cotizaciones.exception;

/**
 * Se lanza cuando una cotización, usuario o componente referenciado no existe
 * (ej. ID de cotización inexistente, o usuario/componente no encontrado vía Feign).
 * Capturada por {@code GlobalExceptionHandler}, que responde HTTP 404 Not Found.
 */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
