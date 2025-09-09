package ru.melulingerie.products.service;

import ru.melulingerie.products.dto.response.PriceQuoteDto;

import java.util.List;
import java.util.Map;

public interface PriceService {
    Map<Long, PriceQuoteDto> getCurrentPrices(List<Long> productIds);
}
