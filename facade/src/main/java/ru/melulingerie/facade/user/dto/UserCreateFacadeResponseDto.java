package ru.melulingerie.facade.user.dto;

public record UserCreateFacadeResponseDto(
    Long userId,
    Long cartId,
    Long wishlistId
) {}
