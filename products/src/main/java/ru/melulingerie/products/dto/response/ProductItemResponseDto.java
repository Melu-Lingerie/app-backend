package ru.melulingerie.products.dto.response;

import java.math.BigDecimal;
import java.util.Set;

public record ProductItemResponseDto(
        Long productId,
        BigDecimal price,
        String name,
        Long mainMediaId,
        Set<String> colors
) {
}
