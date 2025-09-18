package ru.melulingerie.facade.products.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "ProductCardResponse", description = "Карточка товара")
public record ProductCardResponseDto(

        @Schema(
                description = "ID продукта",
                example = "1001",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long productId,

        @Schema(
                description = "Название",
                example = "Бюстгальтер Push-Up 'Aurora'",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String name,

        @Schema(
                description = "Артикул/ID",
                example = "BR-2025-AUR-001",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String articleNumber,

        @Schema(
                description = "Описание товара",
                example = "Мягкая чашка, съемные вкладыши, регулируемые бретели",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String description,

        @Schema(
                description = "Состав материалов",
                example = "Полиамид 80%, Эластан 20%",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String structure,

        @Schema(
                description = "Оценка товара",
                example = "4.7",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Float score,

        @Schema(
                description = "ID категории товара",
                example = "101",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long categoryId,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @ArraySchema(
                arraySchema = @Schema(description = "Список вариантов товара"),
                schema = @Schema(implementation = ProductVariantCardDto.class)
        )
        List<ProductVariantCardDto> productVariants

) {
}
