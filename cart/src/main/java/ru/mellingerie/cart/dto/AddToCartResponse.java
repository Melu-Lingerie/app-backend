package ru.mellingerie.cart.dto;

import lombok.Builder;

/**
 * Упрощенный ответ на добавление товара
 */
@Builder
public record AddToCartResponse(
        Long cartItemId,
        int finalQuantity,
        boolean isNewItem,
        String message
) {
}