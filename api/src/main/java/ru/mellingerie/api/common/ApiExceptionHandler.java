package ru.mellingerie.api.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.mellingerie.facade.wishlist.exception.WishlistFacadeExceptions;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ApiError(ErrorCode.VALIDATION_FAILED, "Validation failed", Map.of()));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiError> handleMissingHeader(MissingRequestHeaderException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiError(ErrorCode.MISSING_USER_ID_HEADER, "Missing required header: X-User-Id", Map.of()));
    }

    @ExceptionHandler(WishlistFacadeExceptions.WishlistItemDuplicateException.class)
    public ResponseEntity<ApiError> handleFacadeDuplicate(WishlistFacadeExceptions.WishlistItemDuplicateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError(ErrorCode.WISHLIST_ITEM_DUPLICATE, ex.getMessage(), Map.of()));
    }

    @ExceptionHandler(WishlistFacadeExceptions.WishlistItemNotFoundException.class)
    public ResponseEntity<ApiError> handleFacadeNotFound(WishlistFacadeExceptions.WishlistItemNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError(ErrorCode.WISHLIST_ITEM_NOT_FOUND, ex.getMessage(), Map.of()));
    }

    @ExceptionHandler(WishlistFacadeExceptions.InvalidIdException.class)
    public ResponseEntity<ApiError> handleFacadeInvalidId(WishlistFacadeExceptions.InvalidIdException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiError(ErrorCode.INVALID_ID, ex.getMessage(), Map.of()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError(ErrorCode.INTERNAL_ERROR, "Internal server error", Map.of("error", ex.getClass().getSimpleName())));
    }
}


