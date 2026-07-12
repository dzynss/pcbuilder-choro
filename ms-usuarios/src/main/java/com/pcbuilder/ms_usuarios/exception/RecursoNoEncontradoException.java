package com.pcbuilder.ms_usuarios.exception;

/**
 * Se lanza desde {@link com.pcbuilder.ms_usuarios.service.UsuarioService} cuando se
 * busca/actualiza/elimina un usuario por ID que no existe. Capturada por
 * {@link GlobalExceptionHandler}, que la traduce a HTTP 404 (NOT_FOUND).
 */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
