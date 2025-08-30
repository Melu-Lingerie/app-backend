package ru.melulingerie.products.dto;

import ru.melulingerie.products.domain.ProductVariant;

import java.math.BigDecimal;
import java.util.List;

public record ProductVariantDto(
        Long id,
        String colorName,
        String size,
        Integer stockQuantity,
        BigDecimal additionalPrice,
        Boolean isAvailable,
        Integer sortOrder,
        List<ProductVariantMediaDto> productVariantMedia
) {

    public ProductVariantDto(ProductVariant productVariant) {
        this(
                productVariant.getId(),
                productVariant.getColorName(),
                productVariant.getSize(),
                productVariant.getStockQuantity(),
                productVariant.getAdditionalPrice(),
                productVariant.getIsAvailable(),
                productVariant.getSortOrder(),
                productVariant.getProductVariantMedia().stream().map(ProductVariantMediaDto::new).toList()
        );
    }
}
