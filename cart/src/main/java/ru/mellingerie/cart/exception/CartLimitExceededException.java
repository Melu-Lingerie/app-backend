package ru.mellingerie.cart.exception;

import lombok.Getter;

/**
 * Исключение, возникающее при превышении лимитов корзины
 */
@Getter
public class CartLimitExceededException extends CartException {
    
    private final String limitType;
    private final int limit;
    private final int current;
    
    public CartLimitExceededException(String limitType, int limit, int current) {
        super(String.format("%s limit exceeded. Limit: %d, Current: %d", 
            limitType, limit, current));
        this.limitType = limitType;
        this.limit = limit;
        this.current = current;
    }

}
