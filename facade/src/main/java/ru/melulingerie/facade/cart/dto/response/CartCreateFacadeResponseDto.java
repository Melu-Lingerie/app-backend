package ru.melulingerie.facade.cart.dto.response;

public record CartCreateFacadeResponseDto(
    Long cartId,
    Long userId,
    String message
) {
}