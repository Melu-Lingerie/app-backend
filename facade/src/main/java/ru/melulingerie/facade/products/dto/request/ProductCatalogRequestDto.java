package ru.melulingerie.facade.products.dto.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import lombok.Builder;
import ru.melulingerie.products.enums.ProductStatus;

import java.math.BigDecimal;
import java.util.Set;

@Schema(name = "ProductCatalogRequestDto", description = "Параметры фильтрации и пагинации каталога товаров")
@Builder
public record ProductCatalogRequestDto(

        @Schema(description = "Имя товара", example = "Трусы")
        String name,

        @Schema(description = "Минимальная цена фильтра", example = "990.00")
        BigDecimal minPrice,

        @Schema(description = "Максимальная цена фильтра", example = "9990.00")
        @Max(Integer.MAX_VALUE)
        BigDecimal maxPrice,

        @ArraySchema(arraySchema = @Schema(description = "Набор идентификаторов категорий"),
                schema = @Schema(description = "ID категории", example = "12"))
        Set<Long> categories,

        @ArraySchema(arraySchema = @Schema(description = "Набор размеров без чашек"),
                schema = @Schema(description = "Размер", example = "M"))
        Set<String> sizes,

        @ArraySchema(arraySchema = @Schema(description = "Набор размеров бюстгальтеров с чашками"),
                schema = @Schema(description = "Размер с чашкой", example = "75B"))
        Set<String> sizesOfBraWithCups,

        @ArraySchema(arraySchema = @Schema(description = "Набор доступных цветов"),
                schema = @Schema(description = "Название цвета", example = "Black"))
        Set<String> colors,

        @Schema(description = "запрашиваемый статус продукта")
        ProductStatus productStatus
) {}

