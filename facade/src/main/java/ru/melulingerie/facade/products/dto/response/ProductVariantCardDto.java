package ru.melulingerie.facade.products.dto.response;

import ru.mellingerie.products.dto.ProductVariantDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record ProductVariantCardDto(
        Long id,
        String colorName,
        String size,
        Integer stockQuantity,
        BigDecimal additionalPrice,
        Boolean isAvailable,
        Integer order,
        List<ProductVariantMediaCardDto> productVariantMedia
) {
    public ProductVariantCardDto (ProductVariantDto dto, Map<Long, String> mediaInfo) {
        this(
                dto.id(),
                dto.colorName(),
                dto.size(),
                dto.stockQuantity(),
                dto.additionalPrice(),
                dto.isAvailable(),
                dto.order(),
                dto.productVariantMedia().stream().map(media -> new ProductVariantMediaCardDto(media, mediaInfo.get(media.mediaId()))).toList()
        );
    }
}
