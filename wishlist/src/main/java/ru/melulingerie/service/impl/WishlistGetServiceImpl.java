package ru.melulingerie.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.dto.WishlistGetResponseDto;
import ru.melulingerie.dto.WishlistItemGetResponseDto;
import ru.melulingerie.service.WishlistGetService;
import ru.melulingerie.util.WishlistValidator;
import ru.melulingerie.domain.WishlistItem;
import ru.melulingerie.repository.WishlistRepository;

import java.util.List;
import java.util.Optional;

/**
 * Доменный сервис для получения wishlist
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistGetServiceImpl implements WishlistGetService {

    private final WishlistValidator validationService;
    private final WishlistRepository wishlistRepository;

    @Override
    public WishlistGetResponseDto getWishlist(Long wishlistId) {
        log.info("Starting get wishlist paged for wishlistId: {}", wishlistId);

        validationService.validatePositiveIdOrThrow(wishlistId);

        List<WishlistItem> wishlistItems = wishlistRepository.findByIdWithItems(wishlistId)
                .map(wishlist ->
                        Optional.ofNullable(wishlist.getWishlistItems())
                                .orElse(List.of()))
                .orElseThrow(() -> new IllegalArgumentException("Wishlist not found with wishlistId: " + wishlistId));

        int totalItems = wishlistItems.size();
        
        List<WishlistItemGetResponseDto> itemDtos = wishlistItems.stream()
                .map(this::toModel)
                .toList();
        
        log.info("Wishlist loaded successfully for wishlistId: {} ", wishlistId);

        return new WishlistGetResponseDto(wishlistId, itemDtos, totalItems);
    }

    private WishlistItemGetResponseDto toModel(WishlistItem wishlistItem) {
        return new WishlistItemGetResponseDto(
                wishlistItem.getId(),
                wishlistItem.getProductId(),
                wishlistItem.getAddedAt()
        );
    }
}