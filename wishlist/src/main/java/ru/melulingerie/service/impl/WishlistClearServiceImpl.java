package ru.melulingerie.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.service.WishlistClearService;
import ru.melulingerie.util.WishlistValidator;
import ru.melulingerie.domain.Wishlist;
import ru.melulingerie.repository.WishlistItemRepository;
import ru.melulingerie.repository.WishlistRepository;

/**
 * Доменный сервис для очистки wishlist
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistClearServiceImpl implements WishlistClearService {

    private final WishlistRepository wishlistRepository;
    private final WishlistValidator validationService;
    private final WishlistItemRepository wishlistItemRepository;

    @Override
    @Transactional
    public int clearWishlist(Long wishlistId) {
        log.info("Starting wishlist clear for wishlistId: {}", wishlistId);

        validationService.validatePositiveIdOrThrow(wishlistId);

        Wishlist wishlist = wishlistRepository.findById(wishlistId)
                .orElse(null);

        if (wishlist == null) {
            log.debug("Wishlist not found for wishlistId: {}, nothing to clear", wishlistId);
            return 0;
        }

        validationService.validateClearRequest(wishlist.getUserId(), wishlist);

        int deletedCount = clearWishlistItems(wishlistId);
        
        if (deletedCount == 0) {
            log.debug("Wishlist already empty for wishlistId: {}", wishlistId);
        }

        log.info("Wishlist cleared successfully for wishlistId: {}, deletedItems: {}", 
                wishlistId, deletedCount);
        
        return deletedCount;
    }

    private int clearWishlistItems(Long wishlistId) {
        log.debug("Deleting all items for wishlistId: {}", wishlistId);
        return wishlistItemRepository.deleteAllByWishlistId(wishlistId);
    }
}