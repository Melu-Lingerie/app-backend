package ru.melulingerie.dto;

import java.time.LocalDateTime;

public record GetWishlistItemResponseDto(
        Long id,
        Long productId,
        Long variantId,
        LocalDateTime addedAt
) {}
