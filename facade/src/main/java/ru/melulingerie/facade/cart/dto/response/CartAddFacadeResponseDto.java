package ru.melulingerie.facade.cart.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.melulingerie.facade.cart.dto.CartOperationType;
import ru.melulingerie.facade.cart.dto.CartTotalsDto;

import java.math.BigDecimal;

@Schema(
        name = "CartAddFacadeResponseDto",
        description = "Результат изменения корзины (добавление/увеличение/уменьшение)"
)
public record CartAddFacadeResponseDto(

        @Schema(
                description = "Идентификатор позиции в корзине",
                example = "55501",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long cartItemId,

        @Schema(
                description = "Итоговое количество данной позиции после операции",
                example = "2",
                minimum = "0",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Integer finalQuantity,

        @Schema(
                description = "Итоговая стоимость позиции (количество × цена за единицу)",
                example = "3980.00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        BigDecimal itemTotalPrice,

        @Schema(
                description = "Агрегированные итоги по корзине (сумма, скидки, доставка и т.д.)",
                implementation = CartTotalsDto.class,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        CartTotalsDto cartTotals,

        @Schema(
                description = "Тип выполненной операции над корзиной",
                implementation = CartOperationType.class,
                example = "ADDED",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        CartOperationType operation

) {}
