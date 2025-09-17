package ru.melulingerie.query.dto.response;

import java.math.BigDecimal;

public record ProductCatalogItemResponseDto(
        Long productId,
        String name,
        BigDecimal price,
        String s3url,
        String productStatus
) {
}
