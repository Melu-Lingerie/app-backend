package ru.melulingerie.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.exception.WishlistExceptions;
import ru.melulingerie.service.WishlistValidationService;
import ru.melulingerie.wishlist.domain.Wishlist;
import ru.melulingerie.wishlist.domain.WishlistItem;
import ru.melulingerie.wishlist.repository.WishlistItemRepository;
import ru.melulingerie.wishlist.repository.WishlistRepository;

/**
 * Доменный сервис для удаления элементов из wishlist
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistItemRemoveDomainService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final WishlistValidationService validationService;

    public void removeWishlistItem(Long userId, Long itemId) {
        log.info("Starting remove wishlist item for userId: {}, itemId: {}", userId, itemId);

        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("Wishlist not found for userId: {}", userId);
                    return new IllegalArgumentException("Incorrect userId: " + userId);
                });

        WishlistItem item = wishlistItemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Wishlist item not found: id={}", itemId);
                    return new WishlistExceptions.WishlistItemNotFoundException(itemId);
                });

        validationService.validateRemoveRequest(userId, itemId, wishlist, item);

        removeWishlistItem(item);

        log.info("Wishlist item removed successfully for userId: {}, itemId: {}", userId, itemId);
    }

    @Transactional
    protected void removeWishlistItem(WishlistItem item) {
        log.debug("Deleting wishlist item: {}", item.getId());
        wishlistItemRepository.delete(item);
    }
}
