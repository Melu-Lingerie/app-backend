package ru.melulingerie.products.service;

import ru.melulingerie.products.dto.ProductInfoResponseDto;
import ru.melulingerie.products.dto.ProductVariantResponseDto;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface ProductService {

    ProductInfoResponseDto getProductInfoById(Long productId);

    ProductVariantResponseDto getProductVariantById(Long variantId);

    Map<Long, Set<String>> findAvailableColorsByProductIds(Collection<Long> productIds);

    /**
     * Получение информации о продуктах по списку ID (batch операция)
     */
    Map<Long, ProductInfoResponseDto> getProductInfoByIds(Collection<Long> productIds);
}
