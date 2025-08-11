package ru.melulingerie.dto;

import java.time.LocalDateTime;

public record WishListItemResponseDto(
        Long id,
        Long productId,
        Long variantId,
        LocalDateTime addedAt
) {}
