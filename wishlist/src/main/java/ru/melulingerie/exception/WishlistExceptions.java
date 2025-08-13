package ru.melulingerie.exception;

public final class WishlistExceptions {

    private WishlistExceptions() {}

    public static class WishlistItemNotFoundException extends RuntimeException {
        public WishlistItemNotFoundException(Long itemId) {
            super("Wishlist item not found: id=" + itemId);
        }
    }

    public static class InvalidIdException extends RuntimeException {
        public InvalidIdException(Long id) { super("Invalid id: " + id); }
    }
}