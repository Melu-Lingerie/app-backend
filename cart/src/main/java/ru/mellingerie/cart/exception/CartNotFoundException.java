package ru.mellingerie.cart.exception;

/**
 * Исключение, возникающее когда корзина не найдена
 */
public class CartNotFoundException extends CartException {

    public CartNotFoundException(String message) {
        super(message);
    }
}
