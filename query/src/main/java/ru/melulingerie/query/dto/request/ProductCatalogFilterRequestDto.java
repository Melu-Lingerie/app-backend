package ru.melulingerie.query.dto.request;

import java.math.BigDecimal;
import java.util.Set;

public record ProductCatalogFilterRequestDto(
        String name,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Set<Long> categories,
        Set<String> sizes,
        Set<String> sizesOfBraWithCups,
        Set<String> colors,
        String productStatus
) {
}
