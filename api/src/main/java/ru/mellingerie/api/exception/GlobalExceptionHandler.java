package ru.mellingerie.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage, (a, b) -> a));
        
        log.debug("Validation failed: {}", fieldErrors);
        ErrorResponse error = ErrorResponse.withValidation(400, "Validation failed", fieldErrors);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(ConstraintViolationException ex) {
        Map<String, String> violations = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(v -> v.getPropertyPath().toString(), ConstraintViolation::getMessage, (a, b) -> a));
        
        log.debug("Constraint violation: {}", violations);
        ErrorResponse error = ErrorResponse.withValidation(400, "Constraint violation", violations);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        String detail = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        log.debug("Malformed request body: {}", detail);
        
        ErrorResponse error = ErrorResponse.withDetail(400, "Malformed request body", 
                                                      ErrorResponse.ErrorCode.MALFORMED_REQUEST, detail);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<ErrorResponse> handleMissingCookie(MissingRequestCookieException ex) {
        String message = "Missing cookie: " + ex.getCookieName();
        log.debug(message);
        
        ErrorResponse error = ErrorResponse.withParameter(400, message, 
                                                         ErrorResponse.ErrorCode.MISSING_COOKIE, ex.getCookieName());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex) {
        String message = "Missing parameter: " + ex.getParameterName();
        log.debug(message);
        
        ErrorResponse error = ErrorResponse.withParameter(400, message, 
                                                         ErrorResponse.ErrorCode.MISSING_COOKIE, ex.getParameterName());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = "Invalid value '" + ex.getValue() + "' for parameter '" + ex.getName() + "'";
        log.debug(message);
        
        ErrorResponse error = ErrorResponse.withParameter(400, message, 
                                                         ErrorResponse.ErrorCode.VALIDATION_FAILED, ex.getName());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidation(HandlerMethodValidationException ex) {
        // Собираем все сообщения об ошибках валидации
        String allErrors = ex.getAllErrors().stream()
                .map(error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Validation failed")
                .collect(Collectors.joining("; "));
        
        log.debug("Handler method validation failed: {}", allErrors);
        
        ErrorResponse error = ErrorResponse.withDetail(400, "Parameter validation failed", 
                                                      ErrorResponse.ErrorCode.VALIDATION_FAILED, allErrors);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        
        ErrorResponse error = ErrorResponse.of(500, "Internal server error", ErrorResponse.ErrorCode.INTERNAL_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}


