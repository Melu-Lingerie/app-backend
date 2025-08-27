package ru.mellingerie.products.dto;

import ru.mellingerie.products.domain.ProductVariantMedia;

public record ProductVariantMediaDto(
        Long mediaId,
        Integer sortOrder
) {
    public ProductVariantMediaDto(ProductVariantMedia productVariantMedia) {
         this(productVariantMedia.getMediaId(), productVariantMedia.getSortOrder());
    }
}
