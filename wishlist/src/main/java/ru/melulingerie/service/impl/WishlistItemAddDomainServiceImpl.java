package ru.melulingerie.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.dto.AddItemToWishlistRequestDto;
import ru.melulingerie.dto.AddItemToWishlistResponseDto;
import ru.melulingerie.service.WishlistItemAddDomainService;
import ru.melulingerie.service.WishlistValidationService;
import ru.melulingerie.wishlist.domain.Wishlist;
import ru.melulingerie.wishlist.domain.WishlistItem;
import ru.melulingerie.wishlist.repository.WishlistItemRepository;
import ru.melulingerie.wishlist.repository.WishlistRepository;

import java.time.LocalDateTime;

/**
 * Доменный сервис для добавления элементов в wishlist
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistItemAddDomainServiceImpl implements WishlistItemAddDomainService {
    
    private static final int MAX_ITEMS = 200;

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final WishlistValidationService wishlistValidationService;

    @Override
    public AddItemToWishlistResponseDto addWishlistItemToWishlist(Long userId, AddItemToWishlistRequestDto request) {
        log.info("Starting add wishlist item for userId: {}, request: {}", userId, request);

        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("Wishlist not found for userId: {}", userId);
                    return new IllegalArgumentException("Incorrect userId: " + userId);
                });

        int currentCount = 0;
        boolean duplicateExists = false;

        if (wishlist != null) {
            currentCount = wishlistItemRepository.findAllByWishlistId(wishlist.getId()).size();
            if (request != null) {
                duplicateExists = wishlistItemRepository
                        .findDuplicate(wishlist.getId(), request.productId(), request.variantId())
                        .isPresent();
            }
        }

        wishlistValidationService.validateAddWishlist(userId, request, wishlist, currentCount, MAX_ITEMS, duplicateExists);

        WishlistItem saved = addWishlistItem(wishlist, request);

        log.info("Wishlist item added successfully for userId: {}, itemId: {}", userId, saved.getId());
        return new AddItemToWishlistResponseDto(saved.getId(), "Added to wishlist");
    }

    protected WishlistItem addWishlistItem(Wishlist wishlist, AddItemToWishlistRequestDto request) {
        log.debug("Creating wishlist item for wishlistId: {}, productId: {}, variantId: {}",
                wishlist.getId(), request.productId(), request.variantId());

        WishlistItem item = new WishlistItem();
        item.setWishlist(wishlist);
        item.setProductId(request.productId());
        item.setVariantId(request.variantId());
        item.setAddedAt(LocalDateTime.now());

        return wishlistItemRepository.save(item);
    }
}