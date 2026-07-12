package com.pcbuilder.ms_resenas.exception;

/**
 * Se lanza cuando una reseña o un componente referenciado no existe (ej. ID de reseña inválido,
 * o el componente no existe en ms-componentes). Capturada por {@code GlobalExceptionHandler#handleNoEncontrado},
 * responde HTTP 404.
 */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
