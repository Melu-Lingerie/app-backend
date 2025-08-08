package ru.mellingerie.cart.exception;

import lombok.Getter;

/**
 * Исключение, возникающее при недостаточном количестве товара на складе
 */
@Getter
public class InsufficientStockException extends CartException {
    
    private final int requestedQuantity;
    private final int availableQuantity;
    
    public InsufficientStockException(int requestedQuantity, int availableQuantity) {
        super(String.format("Insufficient stock. Requested: %d, Available: %d", 
            requestedQuantity, availableQuantity));
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

}
