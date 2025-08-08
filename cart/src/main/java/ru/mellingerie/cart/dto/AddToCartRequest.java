package ru.mellingerie.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AddToCartRequest(
        @NotNull Long productId,
        @NotNull Long variantId,
        @NotNull @Min(1) Integer quantity,
        @NotNull Long productPriceId
) {
}