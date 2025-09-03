package ru.melulingerie.facade.cart.dto;

import ru.melulingerie.dto.CartItemGetResponseDto;

import java.math.BigDecimal;
import java.util.List;

public record CartGetFacadeResponseDto(
        List<CartItemGetResponseDto> items,
        Integer itemsCount,
        BigDecimal totalAmount
) {}