package ru.melulingerie.products.dto;

import ru.melulingerie.products.domain.ProductVariantMedia;

public record ProductVariantMediaDto(
        Long mediaId,
        Integer sortOrder
) {
    public ProductVariantMediaDto(ProductVariantMedia productVariantMedia) {
         this(productVariantMedia.getMediaId(), productVariantMedia.getSortOrder());
    }
}
