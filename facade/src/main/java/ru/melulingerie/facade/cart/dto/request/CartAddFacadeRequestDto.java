package ru.melulingerie.facade.cart.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartAddFacadeRequestDto(
        @NotNull Long productId,
        @NotNull Long variantId,
        @NotNull @Positive Integer quantity
) {}
