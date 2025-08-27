package ru.melulingerie.facade.products.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import ru.mellingerie.products.dto.ProductInfoDto;
import ru.mellingerie.products.dto.ProductVariantDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Schema(name = "ProductCardResponse", description = "Карточка товара")
public record ProductCardResponseDto(
        Long productId,
        String name,
        String articleNumber,
        BigDecimal price,
        String description,
        String structure,
        Float score,
        List<ProductVariantCardDto> productVariants
) {
    public ProductCardResponseDto(ProductInfoDto dto, Map<Long, String> mediaInfo) {
        this(
                dto.productId(),
                dto.name(),
                dto.articleNumber(),
                dto.price(),
                dto.description(),
                dto.structure(),
                dto.score(),
                dto.productVariants().stream().map(variant -> new ProductVariantCardDto(variant, mediaInfo)).toList()
        );
    }
}
