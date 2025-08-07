package ru.mellingerie.cart.exception;

/**
 * Исключение, возникающее когда товар недоступен
 */
public class ProductNotAvailableException extends CartException {
    
    public ProductNotAvailableException(Long productId, Long variantId) {
        super(String.format("Product variant not available. ProductId: %d, VariantId: %d", 
            productId, variantId));
    }
    
    public ProductNotAvailableException(String message) {
        super(message);
    }
}
