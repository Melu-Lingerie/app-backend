package ru.melulingerie.facade.wishlist.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на добавление товара в список желаний")
public record WishlistAddFacadeRequestDto(
        @Schema(description = "Идентификатор товара", example = "123", required = true)
        Long productId
) {}