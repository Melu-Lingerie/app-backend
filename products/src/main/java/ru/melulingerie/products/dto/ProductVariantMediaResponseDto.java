package ru.melulingerie.products.dto;

import ru.melulingerie.products.domain.ProductVariantMedia;

public record ProductVariantMediaResponseDto(
        Long mediaId,
        Integer sortOrder
) {
    public ProductVariantMediaResponseDto(ProductVariantMedia productVariantMedia) {
         this(productVariantMedia.getMediaId(), productVariantMedia.getSortOrder());
    }
}
