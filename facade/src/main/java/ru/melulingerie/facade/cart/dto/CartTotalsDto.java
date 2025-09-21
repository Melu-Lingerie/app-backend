package ru.melulingerie.facade.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(
        name = "CartTotalsDto",
        description = "Агрегированные итоги по корзине: суммы и количество позиций"
)
public record CartTotalsDto(

        @Schema(
                description = "Итоговая сумма по корзине",
                example = "3980.00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        BigDecimal totalAmount,

        @Schema(
                description = "Общее количество позиций в корзине",
                example = "3",
                minimum = "0",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Integer totalItemsCount
) {}
