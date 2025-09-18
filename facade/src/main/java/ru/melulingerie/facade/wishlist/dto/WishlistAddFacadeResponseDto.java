package ru.melulingerie.facade.wishlist.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "WishlistAddFacadeResponseDto", description = "Результат добавления товара в список желаний")
public record WishlistAddFacadeResponseDto(

        @Schema(description = "Идентификатор созданной позиции в списке желаний",
                example = "98765",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long wishlistItemId,

        @Schema(description = "Сообщение о результате операции",
                example = "Товар добавлен в список желаний",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String message

) {}
