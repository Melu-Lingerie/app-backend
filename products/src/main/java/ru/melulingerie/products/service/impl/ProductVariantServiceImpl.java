package ru.melulingerie.products.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.melulingerie.products.domain.ProductVariant;
import ru.melulingerie.products.projection.ProductIdColorProjection;
import ru.melulingerie.products.projection.ProductIdPriceIdProjection;
import ru.melulingerie.products.repository.ProductVariantRepository;
import ru.melulingerie.products.service.ProductVariantService;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository productVariantRepository;

    @Override
    public ProductVariant getVariantById(Long variantId) {
        return productVariantRepository.findById(variantId)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                String.format("ProductVariant was not found by given externalId = %s", variantId)
                        )
                );
    }

    @Override
    public Map<Long/*productId*/, Set<String/*colors*/>> findAvailableColorsForEachProducts(Collection<Long> productIds) {
        Map<Long, Set<String>> result = new HashMap<>();

        List<ProductIdColorProjection> productColors = productVariantRepository.findColorsByProductIds(productIds, true);

        for (ProductIdColorProjection projection : productColors) {
            result.computeIfAbsent(projection.getProductId(), v -> new HashSet<>())
                    .add(projection.getColorName());
        }
        return result;
    }

    public Map<Long/*productId*/, Set<Long/*priceIds*/>> findAvailablePricesForEachProducts(Set<Long> productIds) {
        Map<Long, Set<Long>> result = new HashMap<>();

        List<ProductIdPriceIdProjection> productVariantPrices = productVariantRepository.findPricesByProductIds(productIds, true);

        for (ProductIdPriceIdProjection projection : productVariantPrices) {
            result.computeIfAbsent(projection.getProductId(), v -> new HashSet<>())
                    .add(projection.getPriceId());
        }
        return result;
    }

}
