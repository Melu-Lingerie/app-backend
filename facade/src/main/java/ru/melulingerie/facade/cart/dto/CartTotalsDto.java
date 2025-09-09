package ru.melulingerie.facade.cart.dto;

import java.math.BigDecimal;

public record CartTotalsDto(
        BigDecimal totalAmount,
        Integer totalItemsCount,
        BigDecimal deliveryAmount
) {}