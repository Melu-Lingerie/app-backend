package ru.melulingerie.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.melulingerie.dto.WishlistAddItemRequestDto;
import ru.melulingerie.dto.WishlistAddItemResponseDto;
import ru.melulingerie.service.WishlistAddItemService;
import ru.melulingerie.util.WishlistValidator;
import ru.melulingerie.wishlist.domain.Wishlist;
import ru.melulingerie.wishlist.domain.WishlistItem;
import ru.melulingerie.wishlist.repository.WishlistItemRepository;
import ru.melulingerie.wishlist.repository.WishlistRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Доменный сервис для добавления элементов в wishlist
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistAddItemServiceImpl implements WishlistAddItemService {

    private static final int MAX_ITEMS = 200;

    private final WishlistValidator wishlistValidator;
    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;

    //TODO реализовать кешериование
    @Override
    @Transactional(rollbackOn = Exception.class)
    public WishlistAddItemResponseDto addWishlistItem(Long wishlistId, WishlistAddItemRequestDto request) {

        wishlistValidator.validatePositiveIdOrThrow(wishlistId);

        Wishlist wishlist = wishlistRepository.findByIdWithAllItems(wishlistId)
                .orElseThrow(() -> new IllegalArgumentException("Incorrect wishlistId: " + wishlistId));

        List<WishlistItem> items = wishlist.getWishlistItems();

        boolean duplicate = items.stream()
                .anyMatch(wishlistItem -> wishlistItem.getProductId().equals(request.productId())
                        && wishlistItem.getVariantId().equals(request.variantId()));

        wishlistValidator.validateAddWishlist(wishlist.getUserId(), request, wishlist,
                items.size(), MAX_ITEMS, duplicate);

        WishlistItem entity = mapToEntity(request, wishlist);

        try {
            wishlistItemRepository.save(entity);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Item already exists in wishlist", ex);
        }

        return new WishlistAddItemResponseDto(entity.getId(), "Added to wishlist");
    }

    private WishlistItem mapToEntity(WishlistAddItemRequestDto request, Wishlist wishlist) {
        log.debug("Creating wishlist item for wishlistId: {}, productId: {}, variantId: {}",
                wishlist.getId(), request.productId(), request.variantId());

        WishlistItem item = new WishlistItem();
        item.setWishlist(wishlist);
        item.setProductId(request.productId());
        item.setVariantId(request.variantId());
        item.setAddedAt(LocalDateTime.now());
        return item;
    }
}