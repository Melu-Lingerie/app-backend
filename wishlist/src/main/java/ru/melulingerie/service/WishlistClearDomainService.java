package ru.melulingerie.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.service.WishlistValidationService;
import ru.melulingerie.wishlist.domain.Wishlist;
import ru.melulingerie.wishlist.repository.WishlistItemRepository;
import ru.melulingerie.wishlist.repository.WishlistRepository;

/**
 * Доменный сервис для очистки wishlist
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistClearDomainService {

    private final WishlistRepository wishlistRepository;
    private final WishlistValidationService validationService;
    private final WishlistItemRepository wishlistItemRepository;

    public void clearWishlist(Long userId) {
        log.info("Starting wishlist clear for userId: {}", userId);

        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("Wishlist not found for userId: {}", userId);
                    return new IllegalArgumentException("Incorrect userId: " + userId);
                });

        validationService.validateClearRequest(userId, wishlist);

        clearWishlistItems(wishlist.getId());

        log.info("Wishlist cleared successfully for userId: {}, wishlistId: {}", userId, wishlist.getId());
    }

    protected void clearWishlistItems(Long wishlistId) {
        log.debug("Deleting all items for wishlistId: {}", wishlistId);
        wishlistItemRepository.deleteAllByWishlistId(wishlistId);
    }
}
