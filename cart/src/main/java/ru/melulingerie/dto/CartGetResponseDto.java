package ru.melulingerie.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartGetResponseDto(Long cartId, List<CartItemGetResponseDto> items, Integer itemsCount, BigDecimal totalAmount) {}