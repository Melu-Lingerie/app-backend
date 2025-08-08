package ru.mellingerie.cart.dto;

import lombok.Builder;

@Builder
public record CreateCartResponse(
        Long cartId,
        boolean isNewCart,
        String message
) { }
