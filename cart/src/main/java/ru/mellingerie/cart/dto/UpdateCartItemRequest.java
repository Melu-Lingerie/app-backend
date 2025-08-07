package ru.mellingerie.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * DTO для запроса на обновление элемента корзины
 */
@Builder
public record UpdateCartItemRequest(
        @NotNull @Min(1) Integer quantity,
        @NotNull Long productPriceId
) {
}