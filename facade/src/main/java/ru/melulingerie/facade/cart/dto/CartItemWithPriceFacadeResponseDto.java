package ru.melulingerie.facade.cart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CartItemWithPriceFacadeResponseDto(
        Long itemId,
        Long productId,
        Long variantId,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice,
        LocalDateTime addedAt
) {}