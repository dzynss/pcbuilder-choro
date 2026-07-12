package com.pcbuilder.ms_despachos.exception;

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
 * Manejador global de excepciones (@RestControllerAdvice) de ms_despachos.
 * Traduce las excepciones lanzadas por controller/service a respuestas JSON uniformes
 * con timestamp/status/mensaje.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Despacho o usuario referenciado (vía Feign) inexistente -> 404 Not Found. */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleNoEncontrado(RecursoNoEncontradoException ex) {
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /** Falla de comunicación con ms-usuarios vía Feign -> 502 Bad Gateway. */
    @ExceptionHandler(ErrorComunicacionException.class)
    public ResponseEntity<Map<String, Object>> handleComunicacion(ErrorComunicacionException ex) {
        return construirRespuesta(HttpStatus.BAD_GATEWAY, ex.getMessage());
    }

    /** Estado de seguimiento fuera del dominio permitido -> 400 Bad Request. */
    @ExceptionHandler(EstadoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleEstadoInvalido(EstadoInvalidoException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /** Fallos de Bean Validation (@Valid) en los DTO de request -> 400 Bad Request con mapa campo->error. */
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

    /** Violación de restricción de integridad en la BD (ej. constraint) -> 409 Conflict. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleIntegridad(DataIntegrityViolationException ex) {
        log.warn("Violacion de integridad de datos: {}", ex.getMessage());
        return construirRespuesta(HttpStatus.CONFLICT, "El registro ya existe o viola una restriccion de integridad de datos");
    }

    /** Tipo de parámetro incompatible en la URL (ej. id no numérico) -> 400 Bad Request. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTipoInvalido(MethodArgumentTypeMismatchException ex) {
        String mensaje = "El parametro '" + ex.getName() + "' tiene un valor invalido: " + ex.getValue();
        return construirRespuesta(HttpStatus.BAD_REQUEST, mensaje);
    }

    /** Body JSON mal formado o ilegible -> 400 Bad Request. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleCuerpoInvalido(HttpMessageNotReadableException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, "El cuerpo de la peticion esta mal formado o es ilegible");
    }

    /** Falta un parámetro de request obligatorio (ej. ?estado= en el PATCH) -> 400 Bad Request. */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleParametroFaltante(MissingServletRequestParameterException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, "Falta el parametro requerido: " + ex.getParameterName());
    }

    /** Verbo HTTP no soportado por la ruta -> 405 Method Not Allowed. */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMetodoNoSoportado(HttpRequestMethodNotSupportedException ex) {
        return construirRespuesta(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage());
    }

    /** Cualquier otra excepción no controlada -> 500 Internal Server Error, se loguea el stacktrace completo. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        log.error("Error interno no controlado", ex);
        return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error interno inesperado");
    }

    /** Arma el body JSON estándar (timestamp/status/mensaje) reutilizado por la mayoría de los handlers. */
    private ResponseEntity<Map<String, Object>> construirRespuesta(HttpStatus status, String mensaje) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("mensaje", mensaje);
        return ResponseEntity.status(status).body(body);
    }
}
