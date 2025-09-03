package ru.melulingerie.exception;

public final class CartExceptions {

    private CartExceptions() {}

    public static class CartItemNotFoundException extends RuntimeException {
        public CartItemNotFoundException(Long itemId) {
            super("Cart item not found: id=" + itemId);
        }
    }

    public static class CartNotFoundException extends RuntimeException {
        public CartNotFoundException(Long cartId) {
            super("Cart not found: id=" + cartId);
        }
    }

    public static class InvalidQuantityException extends RuntimeException {
        public InvalidQuantityException(Integer quantity) {
            super("Invalid quantity: " + quantity + ". Quantity must be positive number");
        }
    }

    public static class CartFullException extends RuntimeException {
        public CartFullException(int maxItems) {
            super("Cart is full. Maximum " + maxItems + " items allowed");
        }
    }

    public static class InvalidIdException extends RuntimeException {
        public InvalidIdException(Long id) { 
            super("Invalid id: " + id); 
        }
    }
}