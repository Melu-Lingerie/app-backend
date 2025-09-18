package ru.melulingerie.facade.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserCreateFacadeResponseDto", description = "Результат создания гостевого пользователя")
public record UserCreateFacadeResponseDto(

        @Schema(description = "Идентификатор пользователя", example = "1001")
        Long userId,

        @Schema(description = "Идентификатор корзины, привязанной к пользователю", example = "2001")
        Long cartId,

        @Schema(description = "Идентификатор списка желаний пользователя", example = "3001")
        Long wishlistId

) {}
