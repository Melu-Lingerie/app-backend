package ru.melulingerie.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.exception.WishlistExceptions;
import ru.melulingerie.service.WishlistRemoveItemService;
import ru.melulingerie.util.WishlistValidator;
import ru.melulingerie.domain.Wishlist;
import ru.melulingerie.domain.WishlistItem;
import ru.melulingerie.repository.WishlistItemRepository;
import ru.melulingerie.repository.WishlistRepository;

import java.util.List;

/**
 * Доменный сервис для удаления элементов из wishlist
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistRemoveItemServiceImpl implements WishlistRemoveItemService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final WishlistValidator validationService;

    @Override
    @Transactional
    public void removeWishlistItems(Long wishlistId, List<Long> itemIds) {
        log.info("Starting remove wishlist items for wishlistId: {}, itemIds: {}", wishlistId, itemIds);

        validationService.validatePositiveIdOrThrow(wishlistId);
        
        if (itemIds == null || itemIds.isEmpty()) {
            log.warn("No items to remove for wishlistId: {}", wishlistId);
            return;
        }

        itemIds.forEach(validationService::validatePositiveIdOrThrow);

        Wishlist wishlist = wishlistRepository.findByIdWithItems(wishlistId)
                .orElseThrow(() -> {
                    log.warn("Wishlist not found: wishlistId={}", wishlistId);
                    return new IllegalArgumentException("Wishlist not found with id: " + wishlistId);
                });

        List<WishlistItem> itemsToRemove = wishlist.getWishlistItems().stream()
                .filter(item -> itemIds.contains(item.getId()))
                .toList();

        if (itemsToRemove.isEmpty()) {
            log.warn("No matching items found for removal in wishlistId: {}, itemIds: {}", wishlistId, itemIds);
            throw new WishlistExceptions.WishlistItemNotFoundException(itemIds.getFirst());
        }

        if (itemsToRemove.size() != itemIds.size()) {
            List<Long> foundIds = itemsToRemove.stream().map(WishlistItem::getId).toList();
            List<Long> missingIds = itemIds.stream().filter(id -> !foundIds.contains(id)).toList();
            log.warn("Some items not found for removal in wishlistId: {}, missingIds: {}", wishlistId, missingIds);
            throw new WishlistExceptions.WishlistItemNotFoundException(missingIds.getFirst());
        }

        removeWishlistItemsInternal(itemsToRemove);

        log.info("Wishlist items removed successfully for wishlistId: {}, removed count: {}", wishlistId, itemsToRemove.size());
    }

    private void removeWishlistItemsInternal(List<WishlistItem> items) {
        log.debug("Deleting {} wishlist items", items.size());
        wishlistItemRepository.deleteAll(items);
    }
}