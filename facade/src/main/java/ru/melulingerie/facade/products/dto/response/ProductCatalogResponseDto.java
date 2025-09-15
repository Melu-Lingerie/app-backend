package ru.melulingerie.facade.products.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Set;

@Schema(name = "ProductCatalogResponseDto", description = "Элемент каталога (упрощённая карточка товара)")
public record ProductCatalogResponseDto(

        @Schema(description = "ID продукта", example = "1001")
        Long productId,

        @Schema(description = "Название продукта", example = "Бюстгальтер Push-Up 'Aurora'")
        String name,

        @Schema(description = "Итоговая цена", example = "3990.00")
        BigDecimal price,

        @Schema(description = "ссылка на медиа")
        String s3url,

        //todo рассмотреть возможность использования енам вместо стринги
        @Schema(description = "Доступные цвета")
        Set<String> colors
) {}
