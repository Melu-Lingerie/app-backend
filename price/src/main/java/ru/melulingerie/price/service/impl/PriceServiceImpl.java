package ru.melulingerie.price.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.price.domain.Price;
import ru.melulingerie.price.dto.response.PriceQuoteDto;
import ru.melulingerie.price.repository.PriceRepository;
import ru.melulingerie.price.service.PriceService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PriceServiceImpl implements PriceService {

    private final PriceRepository priceRepository;

    @Override
    public Map<Long/*priceId*/, PriceQuoteDto> getPricesByIds(Set<Long> priceIds) {
        if (priceIds == null || priceIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return priceRepository.findAllByIds(priceIds)
                .stream()
                .collect(
                        Collectors.toMap(
                                Price::getId,
                                p -> new PriceQuoteDto(p.getId(), p.getBaseAmount())
                        )
                );

    }

    @Override
    public PriceQuoteDto getPriceById(Long priceId) {
        Price price = priceRepository.findById(priceId).orElseThrow(
                () -> new EntityNotFoundException("Price with id " + priceId + " not found")
        );

        return new PriceQuoteDto(price.getId(), price.getBaseAmount());
    }
}

