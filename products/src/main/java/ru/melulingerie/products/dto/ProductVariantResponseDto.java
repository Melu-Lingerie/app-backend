package ru.melulingerie.products.dto;

import ru.melulingerie.products.domain.ProductVariant;

import java.util.List;

public record ProductVariantResponseDto(
        Long id,
        String colorName,
        String size,
        Integer stockQuantity,
        Long priceId,
        Boolean isAvailable,
        Integer sortOrder,
        List<ProductVariantMediaResponseDto> productVariantMedia
) {

    public ProductVariantResponseDto(ProductVariant productVariant) {
        this(
                productVariant.getId(),
                productVariant.getColorName(),
                productVariant.getSize(),
                productVariant.getStockQuantity(),
                productVariant.getPriceId(),
                productVariant.getIsAvailable(),
                productVariant.getSortOrder(),
                productVariant.getProductVariantMedia().stream().map(ProductVariantMediaResponseDto::new).toList()
        );
    }
}
