package ru.melulingerie.facade.products.dto.request;

import lombok.Builder;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Set;

@Builder
public record ProductCatalogRequestDto(
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Set<Long> categories,
        Set<String> sizes,
        Set<String> sizesOfBraWithCups,
        Set<String> colors,
        Pageable pageable
) {
}
