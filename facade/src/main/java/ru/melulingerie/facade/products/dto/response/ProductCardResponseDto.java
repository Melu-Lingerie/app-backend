package ru.melulingerie.facade.products.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Schema(name = "ProductCardResponse", description = "Карточка товара для витрины каталога")
public record ProductCardResponseDto(

        @Schema(description = "Название товара", example = "Бюстгальтер Push-Up 'Aurora'")
        String name,

        @Schema(description = "Артикул/идентификатор товара в каталоге", example = "BR-2025-AUR-001")
        String articleNumber,

        @Schema(description = "Цена текущей продажи в валюте магазина", example = "3990.00")
        BigDecimal price,

        @ArraySchema(arraySchema = @Schema(description = "Доступные цвета для выбора"),
                schema = @Schema(description = "Название цвета", example = "Black"))
        Set<String> colors,

        @ArraySchema(arraySchema = @Schema(description = "Доступные размеры для выбора"),
                schema = @Schema(description = "Размер", example = "M"))
        Set<String> sizes,

        @Schema(description = "Идентификатор коллекции, к которой относится товар", example = "12")
        Long collectionId,

        @Schema(description = "Идентификатор категории товара", example = "5")
        Long categoryId,

        @Schema(description = "Краткое описание товара", example = "Мягкая чашка, съемные вкладыши, регулируемые бретели")
        String description,

        @Schema(description = "Состав материалов", example = "Полиамид 80%, Эластан 20%")
        String structure,

        @Schema(description = "Размер изделия на модели", example = "S")
        String sizeOnModel,

        // оплату/доставку обычно описывают на уровне эндпоинта/ответов, потому как это не свойство товара
        @ArraySchema(arraySchema = @Schema(description = "Отзывы покупателей по товару"),
                schema = @Schema(implementation = ProductReviewResponseDto.class))
        List<ProductReviewResponseDto> productReviewDtoList

        // медиа лучше оформить отдельным полем (например, List<String> mediaUrls) с @ArraySchema
) {}
