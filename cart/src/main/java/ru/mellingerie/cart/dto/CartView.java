package ru.mellingerie.cart.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO для представления корзины (только собственные данные)
 */
@Builder
public record CartView(
        Long cartId,
        Long userId,
        List<CartItemView> items,
        int itemsCount,
        int totalQuantity,
        LocalDateTime updatedAt
) {
}