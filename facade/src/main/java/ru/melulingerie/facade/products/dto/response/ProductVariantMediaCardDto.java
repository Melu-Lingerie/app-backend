package ru.melulingerie.facade.products.dto.response;

import ru.mellingerie.products.dto.ProductVariantMediaDto;

public record ProductVariantMediaCardDto(
        Long mediaId,
        Integer order,
        String url
) {
    public ProductVariantMediaCardDto(ProductVariantMediaDto dto, String url) {
        this(dto.mediaId(), dto.order(), url);
    }

}
