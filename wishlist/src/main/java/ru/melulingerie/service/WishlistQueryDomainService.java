package ru.melulingerie.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.dto.WishListItemResponseDto;
import ru.melulingerie.dto.WishlistResponseDto;
import ru.melulingerie.service.WishlistValidationService;
import ru.melulingerie.wishlist.domain.Wishlist;
import ru.melulingerie.wishlist.domain.WishlistItem;
import ru.melulingerie.wishlist.repository.WishlistItemRepository;
import ru.melulingerie.wishlist.repository.WishlistRepository;

import java.util.List;

/**
 * Доменный сервис для получения wishlist
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistQueryDomainService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final WishlistValidationService validationService;

    public WishlistResponseDto getWishlist(Long userId) {
        log.info("Starting get wishlist for userId: {}", userId);

        validationService.validatePositiveIdOrThrow(userId);

        Long wishlistId = wishlistRepository.findByUserId(userId)
                .map(Wishlist::getId)
                .orElse(null);

        if (wishlistId == null) {
            log.debug("Wishlist not found for userId: {}, returning empty model", userId);
            return new WishlistResponseDto(List.of(), 0);
        }

        List<WishListItemResponseDto> items = wishlistItemRepository.findAllByWishlistId(wishlistId)
                .stream()
                .map(this::toModel)
                .toList();

        log.info("Wishlist loaded successfully for userId: {}, wishlistId: {}, itemsCount: {}",
                userId, wishlistId, items.size());

        return new WishlistResponseDto(items, items.size());
    }

    private WishListItemResponseDto toModel(WishlistItem wishlistItem) {
        return new WishListItemResponseDto(
                wishlistItem.getId(),
                wishlistItem.getProductId(),
                wishlistItem.getVariantId(),
                wishlistItem.getAddedAt()
        );
    }
}
