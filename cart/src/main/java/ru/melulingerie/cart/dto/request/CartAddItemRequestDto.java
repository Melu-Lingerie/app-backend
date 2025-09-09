package ru.melulingerie.cart.dto.request;

public record CartAddItemRequestDto(Long productId, Long variantId, Integer quantity) {}