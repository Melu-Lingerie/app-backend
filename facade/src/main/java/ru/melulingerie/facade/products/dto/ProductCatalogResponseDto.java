package ru.melulingerie.facade.products.dto;

import java.math.BigDecimal;

public record ProductCatalogResponseDto(
        Long productId,
        String name,
        BigDecimal price
        //todo url image
) {
}
