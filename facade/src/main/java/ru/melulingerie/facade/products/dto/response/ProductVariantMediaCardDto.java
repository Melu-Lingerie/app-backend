package ru.melulingerie.facade.products.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ProductVariantMediaCardDto", description = "Медиа-файл варианта товара")
public record ProductVariantMediaCardDto(

        @Schema(description = "Идентификатор медиа", example = "345678")
        Long mediaId,

        @Schema(description = "Порядок отображения", example = "1")
        Integer sortOrder,

        @Schema(description = "URL изображения для карточки товара", example = "https://cdn.example.com/images/12345/main.jpg")
        String url

) {
}
