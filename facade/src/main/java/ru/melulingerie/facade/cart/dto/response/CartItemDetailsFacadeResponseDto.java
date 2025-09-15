package ru.melulingerie.facade.cart.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CartItemDetailsFacadeResponseDto(
        Long itemId,
        Long productId,
        Long variantId,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice,
        LocalDateTime addedAt,
        String productName,
        String productSku,
        String variantColor,
        String variantSize,
        String imageUrl,
        Boolean isFavorite
) {}