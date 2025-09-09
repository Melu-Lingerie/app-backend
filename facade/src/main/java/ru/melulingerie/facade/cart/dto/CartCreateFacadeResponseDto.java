package ru.melulingerie.facade.cart.dto;

public record CartCreateFacadeResponseDto(
    Long cartId,
    Long userId,
    String message
) {
}