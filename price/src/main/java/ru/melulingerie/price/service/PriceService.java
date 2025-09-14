package ru.melulingerie.price.service;

import ru.melulingerie.price.dto.response.PriceQuoteDto;

import java.util.Map;
import java.util.Set;

public interface PriceService {
    Map<Long, PriceQuoteDto> getCurrentPrices(Set<Long> priceIds);
}
