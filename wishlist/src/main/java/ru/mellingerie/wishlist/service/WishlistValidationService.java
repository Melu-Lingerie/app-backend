package ru.mellingerie.wishlist.service;

import org.springframework.stereotype.Service;
import ru.mellingerie.wishlist.entity.WishlistItem;
import ru.mellingerie.wishlist.exception.WishlistExceptions.InvalidIdException;
import ru.mellingerie.wishlist.exception.WishlistExceptions.WishlistCapacityExceededException;
import ru.mellingerie.wishlist.exception.WishlistExceptions.WishlistItemDuplicateException;
import ru.mellingerie.wishlist.exception.WishlistExceptions.WishlistItemNotFoundException;

@Service
public class WishlistValidationService {

    public void validatePositiveIdOrThrow(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidIdException(id);
        }
    }

    public void validateCapacityNotExceeded(int currentCount, int maxItems) {
        if (currentCount >= maxItems) {
            throw new WishlistCapacityExceededException(maxItems);
        }
    }

    public void validateDuplicateAbsent(boolean duplicateExists, Long productId, Long variantId) {
        if (duplicateExists) {
            throw new WishlistItemDuplicateException(productId, variantId);
        }
    }

    public WishlistItem requireOwnedByWishlist(WishlistItem item, Long wishlistId, Long itemId) {
        if (item == null) {
            throw new WishlistItemNotFoundException(itemId);
        }
        if (item.getWishlist() == null || !item.getWishlist().getId().equals(wishlistId)) {
            throw new WishlistItemNotFoundException(itemId);
        }
        return item;
    }
}
