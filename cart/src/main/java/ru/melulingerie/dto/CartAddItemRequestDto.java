package ru.melulingerie.dto;

public record CartAddItemRequestDto(Long productId, Long variantId, Integer quantity) {}