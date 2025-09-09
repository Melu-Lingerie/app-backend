package ru.melulingerie.cart.dto.response;

public record CartAddItemResponseDto(Long cartItemId, Integer finalQuantity, String message) {}