package com.pcbuilder.ms_usuarios.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Punto único de manejo de errores del microservicio (@RestControllerAdvice, aplica a
 * todos los controllers): traduce excepciones de negocio (lanzadas por
 * {@link com.pcbuilder.ms_usuarios.service.UsuarioService}), de validación de Bean
 * Validation y errores inesperados a respuestas HTTP JSON consistentes con
 * timestamp/status/mensaje.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Traduce {@link RecursoNoEncontradoException} a HTTP 404 (NOT_FOUND). */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleNoEncontrado(RecursoNoEncontradoException ex) {
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /** Traduce {@link CredencialesInvalidasException} a HTTP 401 (UNAUTHORIZED). */
    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, Object>> handleCredenciales(CredencialesInvalidasException ex) {
        return construirRespuesta(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    /** Traduce {@link SolicitudInvalidaException} a HTTP 400 (BAD_REQUEST). */
    @ExceptionHandler(SolicitudInvalidaException.class)
    public ResponseEntity<Map<String, Object>> handleSolicitudInvalida(SolicitudInvalidaException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /** Captura fallos de validación de {@code @Valid} en los DTO de request y devuelve HTTP 400 con el detalle por campo. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("errores", errores);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /** Traduce violaciones de restricciones de BD (p.ej. correo duplicado) a HTTP 409 (CONFLICT). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleIntegridad(DataIntegrityViolationException ex) {
        log.warn("Violacion de integridad de datos: {}", ex.getMessage());
        return construirRespuesta(HttpStatus.CONFLICT, "El registro ya existe o viola una restriccion de integridad de datos");
    }

    /** Traduce un parámetro de request con tipo inválido (p.ej. ID no numérico) a HTTP 400 (BAD_REQUEST). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTipoInvalido(MethodArgumentTypeMismatchException ex) {
        String mensaje = "El parametro '" + ex.getName() + "' tiene un valor invalido: " + ex.getValue();
        return construirRespuesta(HttpStatus.BAD_REQUEST, mensaje);
    }

    /** Traduce un body JSON mal formado o ilegible a HTTP 400 (BAD_REQUEST). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleCuerpoInvalido(HttpMessageNotReadableException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, "El cuerpo de la peticion esta mal formado o es ilegible");
    }

    /** Traduce la ausencia de un parámetro de request requerido a HTTP 400 (BAD_REQUEST). */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleParametroFaltante(MissingServletRequestParameterException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, "Falta el parametro requerido: " + ex.getParameterName());
    }

    /** Traduce el uso de un método HTTP no soportado por la ruta a HTTP 405 (METHOD_NOT_ALLOWED). */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMetodoNoSoportado(HttpRequestMethodNotSupportedException ex) {
        return construirRespuesta(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage());
    }

    /** Fallback para cualquier excepción no controlada: la registra en log y responde HTTP 500 (INTERNAL_SERVER_ERROR). */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        log.error("Error interno no controlado", ex);
        return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error interno inesperado");
    }

    /** Construye el cuerpo JSON estándar de error (timestamp/status/mensaje) usado por todos los handlers de esta clase. */
    private ResponseEntity<Map<String, Object>> construirRespuesta(HttpStatus status, String mensaje) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("mensaje", mensaje);
        return ResponseEntity.status(status).body(body);
    }
}
