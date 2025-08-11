package ru.melulingerie.dto;

import java.time.LocalDateTime;

public record WishlistItemGetResponseDto(
        Long id,
        Long productId,
        Long variantId,
        LocalDateTime addedAt
) {}