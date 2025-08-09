package ru.mellingerie.api.wishlist;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.mellingerie.api.common.ApiError;
import ru.mellingerie.api.common.ErrorCode;
import ru.mellingerie.exceptions.wishlist.WishlistExceptions;

import java.util.Map;

@RestControllerAdvice
public class WishlistExceptionHandler {

    @ExceptionHandler(WishlistExceptions.WishlistItemDuplicateException.class)
    public ResponseEntity<ApiError> handleDuplicate(WishlistExceptions.WishlistItemDuplicateException ex) {
        return new ResponseEntity<>(new ApiError(ErrorCode.WISHLIST_ITEM_DUPLICATE, ex.getMessage(), Map.of()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(WishlistExceptions.WishlistItemNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(WishlistExceptions.WishlistItemNotFoundException ex) {
        return new ResponseEntity<>(new ApiError(ErrorCode.WISHLIST_ITEM_NOT_FOUND, ex.getMessage(), Map.of()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WishlistExceptions.WishListInvalidIdException.class)
    public ResponseEntity<ApiError> handleInvalidId(WishlistExceptions.WishListInvalidIdException ex) {
        return new ResponseEntity<>(new ApiError(ErrorCode.INVALID_ID, ex.getMessage(), Map.of()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WishlistExceptions.WishlistCapacityExceededException.class)
    public ResponseEntity<ApiError> handleCapacityExceeded(WishlistExceptions.WishlistCapacityExceededException ex) {
        return new ResponseEntity<>(new ApiError(ErrorCode.WISHLIST_CAPACITY_EXCEEDED, ex.getMessage(), Map.of()), HttpStatus.BAD_REQUEST);
    }
}


