package ru.melulingerie.facade.cart.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CartGetFacadeResponseDto(
        List<CartItemDetailsFacadeResponseDto> items,
        Integer itemsCount,
        BigDecimal totalAmount
) {}