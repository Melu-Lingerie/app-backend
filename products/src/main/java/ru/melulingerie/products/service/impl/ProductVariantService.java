package ru.melulingerie.products.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.melulingerie.products.projection.ProductIdColorProjection;
import ru.melulingerie.products.repository.ProductVariantRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductVariantService {

    private final ProductVariantRepository productVariantRepository;

    public Map<Long, Set<String>> findAvailableColorsForEachProducts(Set<Long> productIds) {
        Map<Long, Set<String>> result = new HashMap<>();

        List<ProductIdColorProjection> productColors = productVariantRepository.findColorsByProductIds(productIds, true);

        for(ProductIdColorProjection projection : productColors) {
            result.computeIfAbsent(projection.getProductId(), v -> new HashSet<>())
                    .add(projection.getColorName());
        }
        return result;
    }
}
