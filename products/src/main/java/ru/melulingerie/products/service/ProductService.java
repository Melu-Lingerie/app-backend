package ru.melulingerie.products.service;

import ru.melulingerie.products.dto.ProductInfoResponseDto;
import ru.melulingerie.products.dto.ProductVariantResponseDto;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ProductService {

    ProductInfoResponseDto getProductInfoById(Long productId);

    ProductVariantResponseDto getProductVariantById(Long variantId);

    Map<Long, Set<String>> findAvailableColorsByProductIds(Collection<Long> productIds);

    Map<Long, Long> getCategoryIdByProductIds(List<Long> productIds);
}
