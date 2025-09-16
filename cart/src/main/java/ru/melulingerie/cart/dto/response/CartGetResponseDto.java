package ru.melulingerie.cart.dto.response;

import java.util.List;

public record CartGetResponseDto(Long cartId, List<CartItemGetResponseDto> items, Integer itemsCount) {}