package ru.mellingerie.facade.wishlist.exception;

public final class WishlistFacadeExceptions {
    private WishlistFacadeExceptions() {}

    public static class WishlistItemDuplicateException extends RuntimeException {
        public WishlistItemDuplicateException(Long productId, Long variantId) {
            super("Item already exists in wishlist: productId=" + productId + ", variantId=" + variantId);
        }
    }

    public static class WishlistItemNotFoundException extends RuntimeException {
        public WishlistItemNotFoundException(Long itemId) {
            super("Wishlist item not found: id=" + itemId);
        }
    }

    public static class InvalidIdException extends RuntimeException {
        public InvalidIdException(Long id) {
            super("Invalid id: " + id);
        }
    }

    public static class WishlistCapacityExceededException extends RuntimeException {
        public WishlistCapacityExceededException() {
            super("Wishlist capacity exceeded");
        }
    }
}


