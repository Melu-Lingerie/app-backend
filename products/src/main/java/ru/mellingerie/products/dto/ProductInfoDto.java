package ru.mellingerie.products.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record ProductInfoDto(
        String name,
        String articleNumber,
        BigDecimal price,
        Set<String> colors,
        Set<String> sizes,
        Long collectionId,
        Long categoryId,
        String description,
        String structure,
        String sizeOnModel,
        List<ProductReviewDto> productReviewDtoList
) {
}
