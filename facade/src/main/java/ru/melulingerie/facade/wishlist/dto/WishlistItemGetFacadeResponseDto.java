package ru.melulingerie.facade.wishlist.dto;

import java.time.LocalDateTime;

public record WishlistItemGetFacadeResponseDto(
        Long id,
        Long productId,
        LocalDateTime addedAt
) {}