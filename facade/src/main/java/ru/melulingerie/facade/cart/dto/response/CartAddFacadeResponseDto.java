package ru.melulingerie.facade.cart.dto.response;

import ru.melulingerie.facade.cart.dto.CartOperationType;
import ru.melulingerie.facade.cart.dto.CartTotalsDto;

import java.math.BigDecimal;

public record CartAddFacadeResponseDto(
        Long cartItemId,
        Integer finalQuantity,
        BigDecimal itemTotalPrice,
        CartTotalsDto cartTotals,
        CartOperationType operation
) {}
