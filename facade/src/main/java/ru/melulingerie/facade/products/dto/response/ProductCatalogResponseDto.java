package ru.melulingerie.facade.products.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.melulingerie.products.enums.ProductStatus;

import java.math.BigDecimal;
import java.util.Set;

@Schema(name = "ProductCatalogResponseDto", description = "Элемент каталога (упрощённая карточка товара)")
public record ProductCatalogResponseDto(

        @Schema(description = "ID продукта",
                example = "1001",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long productId,

        @Schema(description = "Название продукта",
                example = "Бюстгальтер Push-Up 'Aurora'",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Итоговая цена",
                example = "3990.00",
                requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal price,

        @Schema(description = "ссылка на медиа",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String s3url,

        //todo рассмотреть возможность использования енам вместо стринги
        @Schema(description = "Доступные цвета",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Set<String> colors,

        @Schema(description = "Статус товара",
                requiredMode = Schema.RequiredMode.REQUIRED)
        ProductStatus productStatus
) {}
