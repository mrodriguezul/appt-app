package com.mrodriguezul.apptapp.web.exception;

import com.mrodriguezul.apptapp.domain.exception.AppointmentConflictException;
import com.mrodriguezul.apptapp.domain.exception.InvalidAppointmentException;
import com.mrodriguezul.apptapp.domain.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidAppointmentException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidAppointment(InvalidAppointmentException ex) {
        return errorResponse("Datos inválidos", ex.getMessage(), 400);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return errorResponse("Recurso no encontrado", ex.getMessage(), 404);
    }

    @ExceptionHandler(AppointmentConflictException.class)
    public ResponseEntity<Map<String, Object>> handleAppointmentConflict(AppointmentConflictException ex) {
        return errorResponse("Conflicto de horario", ex.getMessage(), 409);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        logger.error("Error interno del servidor: ", ex);
        return errorResponse("Internal Server Error", "An unexpected error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDataAccessException(DataAccessException ex) {
        logger.error("Error de acceso a datos: ", ex);
        return errorResponse("Data Access Error", "Database access error", HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        logger.warn("Error de validación: ", ex);
        return errorResponse("Invalid data error", "The provided data is not valid", HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        logger.warn("Error de tipo de parámetro: ", ex);
        return errorResponse("Invalid parameter error", "The provided parameter is not in the correct format", HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        logger.error("Error de tiempo de ejecución: ", ex);
        return errorResponse("Procesing error", "An unexpected error occurred during request processing", HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidJson(HttpMessageNotReadableException ex) {
        logger.error("Error jsonbad request: ", ex);
        return errorResponse("Invalid request body", "The request body is invalid or malformed", HttpStatus.BAD_REQUEST.value());
    }

    private ResponseEntity<Map<String, Object>> errorResponse(String error, String message, int status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("status", status);
        errorResponse.put("timestamp", System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(status));
    }


}
