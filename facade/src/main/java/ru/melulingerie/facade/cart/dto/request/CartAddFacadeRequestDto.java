package ru.melulingerie.facade.cart.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(
        name = "CartAddFacadeRequestDto",
        description = "Запрос на добавление товара в корзину"
)
public record CartAddFacadeRequestDto(

        @Schema(
                description = "Идентификатор товара",
                example = "12345",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull Long productId,

        @Schema(
                description = "Идентификатор варианта товара (цвет/размер)",
                example = "98765",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull Long variantId,

        @Schema(
                description = "Запрашиваемое количество единиц",
                example = "2",
                minimum = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull @Positive Integer quantity

) {}
