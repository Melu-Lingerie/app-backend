package ru.mellingerie.api.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        return new ResponseEntity<>(new ApiError(ErrorCode.VALIDATION_FAILED, "Validation failed", Map.of()), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiError> handleMissingHeader(MissingRequestHeaderException ex) {
        return new ResponseEntity<>(new ApiError(ErrorCode.MISSING_USER_ID_HEADER, "Missing required header: X-User-Id", Map.of()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
        return new ResponseEntity<>(new ApiError(ErrorCode.INTERNAL_ERROR, "Internal server error", Map.of("error", ex.getClass().getSimpleName())), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}