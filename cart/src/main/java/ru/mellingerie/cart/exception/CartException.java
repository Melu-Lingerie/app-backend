package ru.mellingerie.cart.exception;

/**
 * Базовое исключение для модуля корзины
 */
public abstract class CartException extends RuntimeException {
    
    public CartException(String message) {
        super(message);
    }
    
}
