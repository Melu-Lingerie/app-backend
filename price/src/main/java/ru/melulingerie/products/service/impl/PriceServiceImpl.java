package ru.melulingerie.products.service.impl;

// PriceServiceImpl.java
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.products.domain.Price;
import ru.melulingerie.products.domain.Promotion;
import ru.melulingerie.products.domain.PromotionCondition;
import ru.melulingerie.products.dto.response.PriceQuoteDto;
import ru.melulingerie.products.repository.PriceRepository;
import ru.melulingerie.products.repository.PromotionRepository;
import ru.melulingerie.products.service.PriceService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PriceServiceImpl implements PriceService {

    private final PriceRepository priceRepository;
    private final PromotionRepository promotionRepository;

    private static final int MONEY_SCALE = 2;
    private static final RoundingMode MONEY_ROUNDING = RoundingMode.HALF_UP;

    @Override
    public Map<Long, PriceQuoteDto> getCurrentPrices(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Instant now = Instant.now();

        // 1) Актуальные базовые цены одним запросом (PostgreSQL DISTINCT ON)
        long[] ids = productIds.stream().mapToLong(Long::longValue).toArray();
        List<Price> currentPrices = priceRepository.findCurrentByProductIds(ids, now);

        // Сформировать карту productId -> базовая цена
        Map<Long, Price> priceByProduct = currentPrices.stream()
                .collect(Collectors.toMap(Price::getProductVariantId, p -> p));

        // 2) Активные промо для области PRODUCT одной выборкой
        List<Promotion> active = promotionRepository.findActiveByScope("PRODUCT", now);

        // 3) Для каждого товара рассчитать финальную цену
        Map<Long, PriceQuoteDto> result = new LinkedHashMap<>();
        for (Long pid : productIds) {
            Price basePrice = priceByProduct.get(pid);
            if (basePrice == null) {
                continue; // нет базовой цены — можно пропустить или положить null/ошибку по политике
            }
            BigDecimal base = basePrice.getMoney().getAmount().setScale(MONEY_SCALE, MONEY_ROUNDING);
            String currency = basePrice.getMoney().getCurrency();

            // Применимые промо по условиям
            List<Promotion> applicable = active.stream()
                    .filter(pr -> isApplicable(pr, pid))
                    .collect(Collectors.toList());

            BigDecimal finalAmount = applyPromotions(base, applicable);
            List<Long> appliedIds = collectAppliedIds(base, applicable);

            result.put(pid, new PriceQuoteDto(currency, base, finalAmount, appliedIds));
        }

        return result;
    }

    private boolean isApplicable(Promotion pr, Long productId) {
        if (pr.getConditions() == null || pr.getConditions().isEmpty()) return true;
        return pr.getConditions().stream().allMatch(c -> {
            if (Objects.requireNonNull(c.getKind()) == PromotionCondition.ConditionKind.PRODUCT_ID) {
                String arg = c.getArgValue();
                return switch (c.getOp()) {
                    case EQ -> arg != null && arg.equals(String.valueOf(productId));
                    case IN -> arg != null && (("," + arg + ",").contains("," + productId + ","));
                    default -> true;
                };
            } else {
                return true;
            }
        });
    }

    private BigDecimal applyPromotions(BigDecimal base, List<Promotion> promotions) {
        BigDecimal current = base;
        for (Promotion pr : promotions) {
            BigDecimal after = switch (pr.getType()) {
                case PERCENT -> {
                    BigDecimal pct = Optional.ofNullable(pr.getValue()).orElse(BigDecimal.ZERO);
                    BigDecimal discount = current.multiply(pct)
                            .divide(new BigDecimal("100"), MONEY_SCALE + 2, MONEY_ROUNDING);
                    yield current.subtract(discount);
                }
                case AMOUNT -> current.subtract(Optional.ofNullable(pr.getValue()).orElse(BigDecimal.ZERO));
                case BOGO, TIERED -> current; // упрощение: сложные правила добавить по мере необходимости
            };
            BigDecimal clipped = after.max(BigDecimal.ZERO).setScale(MONEY_SCALE, MONEY_ROUNDING);
            if (clipped.compareTo(current) < 0) {
                current = clipped;
                if (!pr.isStackable()) break;
            }
        }
        return current.setScale(MONEY_SCALE, MONEY_ROUNDING);
    }

    private List<Long> collectAppliedIds(BigDecimal base, List<Promotion> promotions) {
        List<Long> ids = new ArrayList<>();
        BigDecimal current = base;
        for (Promotion pr : promotions) {
            BigDecimal after = switch (pr.getType()) {
                case PERCENT -> {
                    BigDecimal pct = Optional.ofNullable(pr.getValue()).orElse(BigDecimal.ZERO);
                    BigDecimal discount = current.multiply(pct)
                            .divide(new BigDecimal("100"), MONEY_SCALE + 2, MONEY_ROUNDING);
                    yield current.subtract(discount);
                }
                case AMOUNT -> current.subtract(Optional.ofNullable(pr.getValue()).orElse(BigDecimal.ZERO));
                case BOGO, TIERED -> current;
            };
            BigDecimal clipped = after.max(BigDecimal.ZERO).setScale(MONEY_SCALE, MONEY_ROUNDING);
            if (clipped.compareTo(current) < 0) {
                ids.add(pr.getId());
                current = clipped;
                if (!pr.isStackable()) break;
            }
        }
        return ids;
    }
}

