package ru.mellingerie.api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        @NotNull String message,
        @NotNull Integer status,
        @NotNull OffsetDateTime timestamp,
        @NotNull String path,
        String traceId,
        Map<String, String> fieldErrors
) {

    public static ErrorResponse of(int status, String message, String path) {
        return new ErrorResponse(message, status, OffsetDateTime.now(), path,
                getCurrentTraceId(), null);
    }

    public static ErrorResponse validation(String message, String path, Map<String, String> fieldErrors) {
        return new ErrorResponse(message, HttpStatus.BAD_REQUEST.value(), OffsetDateTime.now(),
                path, getCurrentTraceId(), fieldErrors);
    }

    private static String getCurrentTraceId() {
        return MDC.get("traceId") != null ? MDC.get("traceId") : UUID.randomUUID().toString();
    }
}


