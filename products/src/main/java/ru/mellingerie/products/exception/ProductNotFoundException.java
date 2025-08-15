package ru.mellingerie.products.exception;

public class ProductNotFoundException extends RuntimeException {
    
    public ProductNotFoundException(String message) {
        super(message);
    }
    
    public ProductNotFoundException(Long productId) {
        super("Product with id " + productId + " not found");
    }
    
    public ProductNotFoundException(String slug, String type) {
        super("Product with " + type + " '" + slug + "' not found");
    }
} 