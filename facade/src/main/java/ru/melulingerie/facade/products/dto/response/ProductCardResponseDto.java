package ru.melulingerie.facade.products.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "ProductCardResponse", description = "Карточка товара")
public record ProductCardResponseDto(

        @Schema(description = "ID продукта", example = "1001")
        Long productId,

        @Schema(description = "Название", example = "Бюстгальтер Push-Up 'Aurora'")
        String name,

        @Schema(description = "Артикул/ID", example = "BR-2025-AUR-001")
        String articleNumber,

        @Schema(description = "Описание товара", example = "Мягкая чашка, съемные вкладыши, регулируемые бретели")
        String description,

        @Schema(description = "Состав материалов", example = "Полиамид 80%, Эластан 20%")
        String structure,

        @Schema(description = "Оценка товара", example = "4.7")
        Float score,

        @ArraySchema(arraySchema = @Schema(description = "Список вариантов товара"),
                schema = @Schema(implementation = ProductVariantCardDto.class))
        List<ProductVariantCardDto> productVariants

) {
}
