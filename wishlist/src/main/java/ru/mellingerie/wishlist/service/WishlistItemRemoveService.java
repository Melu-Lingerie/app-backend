package ru.mellingerie.wishlist.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mellingerie.wishlist.entity.Wishlist;
import ru.mellingerie.wishlist.exception.WishlistExceptions.InvalidIdException;
import ru.mellingerie.wishlist.exception.WishlistExceptions.WishlistItemNotFoundException;
import ru.mellingerie.wishlist.repository.WishlistItemRepository;
import ru.mellingerie.wishlist.repository.WishlistRepository;

@Service
@RequiredArgsConstructor
public class WishlistItemRemoveService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final WishlistValidationService validationService;

    @Transactional
    public void remove(Long userId, Long itemId) {
        validationService.validatePositiveIdOrThrow(itemId);
        validationService.validatePositiveIdOrThrow(userId);
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new InvalidIdException(userId));
        var entity = wishlistItemRepository.findById(itemId).orElse(null);
        entity = validationService.requireOwnedByWishlist(entity, wishlist.getId(), itemId);
        wishlistItemRepository.delete(entity);
    }
}


