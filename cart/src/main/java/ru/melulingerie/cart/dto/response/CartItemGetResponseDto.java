package ru.melulingerie.cart.dto.response;

import java.time.LocalDateTime;

public record CartItemGetResponseDto(
        Long itemId,
        Long productId,
        Long variantId,
        Integer quantity,
        LocalDateTime addedAt
) {}