package ru.melulingerie.cart.dto.response;

public record CartCreateResponseDto(
    Long cartId,
    Long userId,
    String message
) {
}