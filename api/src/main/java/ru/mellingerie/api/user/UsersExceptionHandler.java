package ru.mellingerie.api.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.mellingerie.api.common.ApiError;
import ru.mellingerie.api.common.ErrorCode;

import java.util.Map;

@RestControllerAdvice
public class UsersExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(ErrorCode.VALIDATION_FAILED, ex.getMessage(), Map.of()));
    }
}


