package ru.mellingerie.wishlist.service;

import org.springframework.stereotype.Service;
import ru.mellingerie.exceptions.wishlist.WishlistExceptions;
import ru.mellingerie.wishlist.entity.WishlistItem;

@Service
public class WishlistValidationService {

    public void validatePositiveIdOrThrow(Long id) {
        if (id == null || id <= 0) {
            throw new WishlistExceptions.WishListInvalidIdException(id);
        }
    }

    public void validateCapacityNotExceeded(int currentCount, int maxItems) {
        if (currentCount >= maxItems) {
            throw new WishlistExceptions.WishlistCapacityExceededException(maxItems);
        }
    }

    public void validateDuplicateAbsent(boolean duplicateExists, Long productId, Long variantId) {
        if (duplicateExists) {
            throw new WishlistExceptions.WishlistItemDuplicateException(productId, variantId);
        }
    }

    public WishlistItem requireOwnedByWishlist(WishlistItem item, Long wishlistId, Long itemId) {
        if (item == null) {
            throw new WishlistExceptions.WishlistItemNotFoundException(itemId);
        }
        if (item.getWishlist() == null || !item.getWishlist().getId().equals(wishlistId)) {
            throw new WishlistExceptions.WishlistItemNotFoundException(itemId);
        }
        return item;
    }
}
