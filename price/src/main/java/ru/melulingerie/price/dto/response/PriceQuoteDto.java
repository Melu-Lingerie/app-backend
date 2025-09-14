package ru.melulingerie.price.dto.response;

import java.math.BigDecimal;

public record PriceQuoteDto(
        Long priceId,
        BigDecimal price
) {
}
