package com.pcbuilder.ms_login.exception;

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
 * Manejador global de excepciones (@RestControllerAdvice) para todos los controllers de
 * ms_login. Traduce cada excepción a una respuesta JSON uniforme con timestamp/status/mensaje.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Credenciales incorrectas (validadas contra ms-usuarios) → 401 UNAUTHORIZED. */
    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, Object>> handleCredenciales(CredencialesInvalidasException ex) {
        return construirRespuesta(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    /** Fallo de comunicación Feign con ms-usuarios (timeout, 5xx, etc.) → 502 BAD_GATEWAY. */
    @ExceptionHandler(ErrorComunicacionException.class)
    public ResponseEntity<Map<String, Object>> handleComunicacion(ErrorComunicacionException ex) {
        return construirRespuesta(HttpStatus.BAD_GATEWAY, ex.getMessage());
    }

    /** Errores de validación de Bean Validation (@Valid en el DTO de login) → 400 BAD_REQUEST con detalle por campo. */
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

    /** Violación de restricción de datos (BD) → 409 CONFLICT. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleIntegridad(DataIntegrityViolationException ex) {
        log.warn("Violacion de integridad de datos: {}", ex.getMessage());
        return construirRespuesta(HttpStatus.CONFLICT, "El registro ya existe o viola una restriccion de integridad de datos");
    }

    /** Parámetro con tipo inválido en la petición → 400 BAD_REQUEST. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTipoInvalido(MethodArgumentTypeMismatchException ex) {
        String mensaje = "El parametro '" + ex.getName() + "' tiene un valor invalido: " + ex.getValue();
        return construirRespuesta(HttpStatus.BAD_REQUEST, mensaje);
    }

    /** Cuerpo de la petición mal formado o ilegible (JSON inválido) → 400 BAD_REQUEST. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleCuerpoInvalido(HttpMessageNotReadableException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, "El cuerpo de la peticion esta mal formado o es ilegible");
    }

    /** Falta un parámetro requerido en la petición → 400 BAD_REQUEST. */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleParametroFaltante(MissingServletRequestParameterException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, "Falta el parametro requerido: " + ex.getParameterName());
    }

    /** Método HTTP no soportado por el endpoint → 405 METHOD_NOT_ALLOWED. */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMetodoNoSoportado(HttpRequestMethodNotSupportedException ex) {
        return construirRespuesta(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage());
    }

    /** Fallback para cualquier excepción no controlada explícitamente → 500 INTERNAL_SERVER_ERROR. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        log.error("Error interno no controlado", ex);
        return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error interno inesperado");
    }

    /** Arma el cuerpo JSON uniforme (timestamp/status/mensaje) usado por todos los handlers. */
    private ResponseEntity<Map<String, Object>> construirRespuesta(HttpStatus status, String mensaje) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("mensaje", mensaje);
        return ResponseEntity.status(status).body(body);
    }
}
