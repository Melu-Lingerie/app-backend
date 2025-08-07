package ru.mellingerie.cart.dto;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * DTO для элемента корзины (только собственные данные)
 */
@Builder
public record CartItemView(
        Long itemId,
        Long productId,
        Long variantId,
        int quantity,
        Long productPriceId,
        LocalDateTime addedAt
) {
}