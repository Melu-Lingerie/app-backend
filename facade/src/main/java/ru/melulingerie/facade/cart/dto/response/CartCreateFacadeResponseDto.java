package ru.melulingerie.facade.cart.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "CartCreateFacadeResponseDto",
        description = "Результат создания корзины"
)
public record CartCreateFacadeResponseDto(

        @Schema(
                description = "Идентификатор новой корзины",
                example = "123",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long cartId,

        @Schema(
                description = "Идентификатор пользователя, к которому привязана корзина",
                example = "1001",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long userId,

        @Schema(
                description = "Статусное сообщение о создании корзины",
                example = "Cart created",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String message

) {}
