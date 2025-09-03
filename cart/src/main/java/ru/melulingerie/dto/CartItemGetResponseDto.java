package ru.melulingerie.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CartItemGetResponseDto(
        Long itemId,
        Long productId,
        Long variantId,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice,
        LocalDateTime addedAt
) {}