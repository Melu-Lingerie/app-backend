package ru.melulingerie.products.service;

import ru.melulingerie.products.domain.ProductVariant;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface ProductVariantService {
    ProductVariant getVariantById(Long variantId);

    Map<Long, Set<String>> findAvailableColorsForEachProducts(Collection<Long> productIds);

    Map<Long, ProductVariant> getVariantsByIds(Collection<Long> variantIds);
}
