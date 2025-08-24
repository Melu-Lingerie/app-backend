package ru.mellingerie.products.dto.response;

import java.math.BigDecimal;

public record ProductItemResponseDto(
        Long productId,
        BigDecimal price,
        String name
) {
}
