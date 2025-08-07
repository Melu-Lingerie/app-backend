package ru.mellingerie.cart.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для модуля корзины
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Обработка исключений корзины
     */
    @ExceptionHandler(CartException.class)
    public ResponseEntity<Map<String, Object>> handleCartException(CartException ex, WebRequest request) {
        log.warn("Cart exception occurred: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("error", "Cart operation failed");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("path", request.getDescription(false));
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Обработка исключений не найденных товаров корзины
     */
    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCartItemNotFoundException(CartItemNotFoundException ex, WebRequest request) {
        log.warn("Cart item not found: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("error", "Cart item not found");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("path", request.getDescription(false));
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Обработка исключений не найденных корзин
     */
    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCartNotFoundException(CartNotFoundException ex, WebRequest request) {
        log.warn("Cart not found: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("error", "Cart not found");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("path", request.getDescription(false));
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Обработка исключений недостаточного количества товара
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientStockException(InsufficientStockException ex, WebRequest request) {
        log.warn("Insufficient stock: requested={}, available={}", ex.getRequestedQuantity(), ex.getAvailableQuantity());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("error", "Insufficient stock");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("requestedQuantity", ex.getRequestedQuantity());
        errorResponse.put("availableQuantity", ex.getAvailableQuantity());
        errorResponse.put("path", request.getDescription(false));
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Обработка исключений недоступности товара
     */
    @ExceptionHandler(ProductNotAvailableException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotAvailableException(ProductNotAvailableException ex, WebRequest request) {
        log.warn("Product not available: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("error", "Product not available");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("path", request.getDescription(false));
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Обработка исключений превышения лимитов
     */
    @ExceptionHandler(CartLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleCartLimitExceededException(CartLimitExceededException ex, WebRequest request) {
        log.warn("Cart limit exceeded: {} limit={}, current={}", ex.getLimitType(), ex.getLimit(), ex.getCurrent());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("error", "Cart limit exceeded");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("limitType", ex.getLimitType());
        errorResponse.put("limit", ex.getLimit());
        errorResponse.put("current", ex.getCurrent());
        errorResponse.put("path", request.getDescription(false));
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Обработка исключений не найденной цены
     */
    @ExceptionHandler(ProductPriceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProductPriceNotFoundException(ProductPriceNotFoundException ex, WebRequest request) {
        log.warn("Product price not found: {}", ex.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.NOT_FOUND.value());
        errorResponse.put("error", "Not Found");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("path", request.getDescription(false));

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Обработка общих исключений
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred", ex);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("error", "Internal server error");
        errorResponse.put("message", "An unexpected error occurred");
        errorResponse.put("path", request.getDescription(false));
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
