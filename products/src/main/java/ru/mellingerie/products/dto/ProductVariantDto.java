package ru.mellingerie.products.dto;

import ru.mellingerie.products.domain.ProductVariant;

import java.math.BigDecimal;
import java.util.List;

public record ProductVariantDto(
        Long id,
        String colorName,
        String size,
        Integer stockQuantity,
        BigDecimal additionalPrice,
        Boolean isAvailable,
        Integer order,
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
                productVariant.getOrder(),
                productVariant.getProductVariantMedia().stream().map(ProductVariantMediaDto::new).toList()
        );
    }
}
