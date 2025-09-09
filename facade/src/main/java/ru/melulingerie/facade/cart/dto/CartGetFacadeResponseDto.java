package ru.melulingerie.facade.cart.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartGetFacadeResponseDto(
        List<CartItemWithPriceFacadeResponseDto> items,
        Integer itemsCount,
        BigDecimal totalAmount
) {}