package com.example.inventory_app.Config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador global de excepciones para manejar errores de manera consistente.
 *
 * @author DamianG
 * @version 1.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones de tipo RuntimeException.
     * @param ex La excepción
     * @param request La petición web
     * @return ResponseEntity con el error
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());
        error.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de tipo IllegalArgumentException.
     * @param ex La excepción
     * @param request La petición web
     * @return ResponseEntity con el error
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());
        error.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones generales.
     * @param ex La excepción
     * @param request La petición web
     * @return ResponseEntity con el error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex, WebRequest request) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.put("error", "Internal Server Error");
        error.put("message", "Ha ocurrido un error interno del servidor");
        error.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
