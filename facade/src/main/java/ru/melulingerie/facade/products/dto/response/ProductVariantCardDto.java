package ru.melulingerie.facade.products.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(name = "ProductVariantCardDto", description = "Вариант товара для карточки (цвет/размер и атрибуты наличия)")
public record ProductVariantCardDto(

        @Schema(
                description = "ID варианта товара",
                example = "101",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long id,

        @Schema(
                description = "Название цвета",
                example = "Black",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String colorName,

        @Schema(
                description = "Размер",
                example = "M",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String size,

        @Schema(
                description = "Остаток на складе",
                example = "25",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Integer stockQuantity,

        @Schema(
                description = "Доплата к базовой цене для данного варианта",
                example = "200.00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        BigDecimal price,

        @Schema(
                description = "Признак доступности к заказу",
                example = "true",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Boolean isAvailable,

        @Schema(
                description = "Порядок отображения варианта",
                example = "3",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Integer sortOrder,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @ArraySchema(
                arraySchema = @Schema(description = "Медиа файлы, связанные с вариантом"),
                schema = @Schema(implementation = ProductVariantMediaCardDto.class)
        )
        List<ProductVariantMediaCardDto> productVariantMedia
) {
}
