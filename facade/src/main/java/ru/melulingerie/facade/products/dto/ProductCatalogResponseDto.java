package ru.melulingerie.facade.products.dto;

import java.math.BigDecimal;

public record ProductCatalogResponseDto(
        //url image?????
        Long productId,
        String name,
        BigDecimal price
) {
}
