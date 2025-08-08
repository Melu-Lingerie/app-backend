package ru.mellingerie.facade.wishlist.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddToWishlistRequestDto(
        @NotNull @Positive Long productId,
        @NotNull @Positive Long variantId
) {}


