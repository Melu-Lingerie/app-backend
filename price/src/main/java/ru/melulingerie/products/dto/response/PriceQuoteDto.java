package ru.melulingerie.products.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record PriceQuoteDto(
        String currency,
        BigDecimal baseAmount,
        BigDecimal finalAmount,
        List<Long> appliedPromotionIds
) {
}
