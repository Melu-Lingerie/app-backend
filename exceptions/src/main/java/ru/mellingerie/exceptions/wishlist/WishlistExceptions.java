package ru.mellingerie.exceptions.wishlist;

/**
 * Shared wishlist exceptions to be used across modules.
 */
public final class WishlistExceptions {
    private WishlistExceptions() {}

    public static class WishlistItemDuplicateException extends RuntimeException {
        public WishlistItemDuplicateException(Long productId, Long variantId) {
            super("Item already exists: productId=" + productId + ", variantId=" + variantId);
        }
    }

    public static class WishlistItemNotFoundException extends RuntimeException {
        public WishlistItemNotFoundException(Long itemId) {
            super("Wishlist item not found: id=" + itemId);
        }
    }

    public static class WishListInvalidIdException extends RuntimeException {
        public WishListInvalidIdException(Long id) {
            super("Invalid id: " + id);
        }
    }

    public static class WishlistCapacityExceededException extends RuntimeException {
        public WishlistCapacityExceededException(int maxItems) {
            super("Wishlist capacity exceeded. Max items: " + maxItems);
        }
    }
}


