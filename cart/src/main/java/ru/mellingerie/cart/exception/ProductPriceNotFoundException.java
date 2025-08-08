package ru.mellingerie.cart.exception;

public class ProductPriceNotFoundException extends CartException {
    public ProductPriceNotFoundException(Long productId) {
        super("Price for product with ID " + productId + " not found.");
    }
}

