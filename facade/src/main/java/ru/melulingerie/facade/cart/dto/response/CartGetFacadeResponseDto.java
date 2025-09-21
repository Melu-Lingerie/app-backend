package ru.melulingerie.facade.cart.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(
        name = "CartGetFacadeResponseDto",
        description = "Содержимое корзины и агрегированные значения"
)
public record CartGetFacadeResponseDto(

        @ArraySchema(
                arraySchema = @Schema(
                        description = "Список позиций корзины",
                        requiredMode = Schema.RequiredMode.REQUIRED
                ),
                schema = @Schema(
                        implementation = CartItemDetailsFacadeResponseDto.class,
                        description = "Позиция корзины"
                ),
                minItems = 0
        )
        List<CartItemDetailsFacadeResponseDto> items,

        @Schema(
                description = "Общее количество позиций в корзине",
                example = "3",
                minimum = "0",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Integer itemsCount,

        @Schema(
                description = "Итоговая сумма по корзине",
                example = "1990.00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        BigDecimal totalAmount

) {}
