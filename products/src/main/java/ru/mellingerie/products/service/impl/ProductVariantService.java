package ru.mellingerie.products.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mellingerie.products.projection.ProductIdColorProjection;
import ru.mellingerie.products.repository.ProductVariantRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductVariantService {

    private final ProductVariantRepository productVariantRepository;

    public Map<Long, Set<String>> findAvailableColorsForEachProducts(Set<Long> productIds) {
        Map<Long, Set<String>> result = new HashMap<>();

        List<ProductIdColorProjection> colorsByProductIds = productVariantRepository.findColorsByProductIds(productIds, true);

        for(ProductIdColorProjection projection : colorsByProductIds) {
            result.computeIfAbsent(projection.getProductId(), v -> new HashSet<>())
                    .add(projection.getColorName());
        }
        return result;
    }
}
