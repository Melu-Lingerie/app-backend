package ru.mellingerie.cart.exception;

/**
 * Исключение, возникающее когда товар в корзине не найден
 */
public class CartItemNotFoundException extends CartException {
    
    public CartItemNotFoundException(Long cartItemId) {
        super(String.format("Cart item with id %d not found", cartItemId));
    }

    public CartItemNotFoundException(Long cartItemId, Long userId) {
        super(String.format("Cart item with id %d and with userId %d not found", cartItemId, userId));
    }
    
}
