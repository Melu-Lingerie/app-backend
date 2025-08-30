package ru.melulingerie.products.dto.request;

import lombok.Builder;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Set;

@Builder
public record ProductFilterRequestDto(
        String name,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Set<Long> categories,
        Set<String> sizes,
        Set<String> sizesOfBraWIthCups,
        Set<String> colors,
        Boolean isActive,
        Boolean onlyAvailableVariants,
        Pageable pageable
) {
}
