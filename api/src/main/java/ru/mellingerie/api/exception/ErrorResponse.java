package ru.mellingerie.api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        @NotNull String message,
        @NotNull Integer status,
        @NotNull OffsetDateTime timestamp,
        ErrorCode errorCode,
        ValidationErrors validationErrors,
        String parameter,
        String detail
) {
    
    public static ErrorResponse of(int status, String message, ErrorCode errorCode) {
        return new ErrorResponse(message, status, OffsetDateTime.now(), errorCode, null, null, null);
    }
    
    public static ErrorResponse withValidation(int status, String message, Map<String, String> fieldErrors) {
        return new ErrorResponse(message, status, OffsetDateTime.now(), 
                                ErrorCode.VALIDATION_FAILED, 
                                new ValidationErrors(fieldErrors), null, null);
    }
    
    public static ErrorResponse withParameter(int status, String message, ErrorCode errorCode, String parameter) {
        return new ErrorResponse(message, status, OffsetDateTime.now(), errorCode, null, parameter, null);
    }
    
    public static ErrorResponse withDetail(int status, String message, ErrorCode errorCode, String detail) {
        return new ErrorResponse(message, status, OffsetDateTime.now(), errorCode, null, null, detail);
    }
    
    public record ValidationErrors(Map<String, String> fields) {}
    
    public enum ErrorCode {
        VALIDATION_FAILED,
        MALFORMED_REQUEST,
        CONSTRAINT_VIOLATION,
        MISSING_COOKIE,
        DATA_CONFLICT,
        INTERNAL_ERROR
    }
}


