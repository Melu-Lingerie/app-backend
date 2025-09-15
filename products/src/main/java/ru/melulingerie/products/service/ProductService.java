package ru.melulingerie.products.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.melulingerie.products.dto.ProductInfoResponseDto;
import ru.melulingerie.products.dto.ProductVariantResponseDto;
import ru.melulingerie.products.dto.request.ProductFilterRequestDto;
import ru.melulingerie.products.dto.response.ProductItemResponseDto;

import java.util.Collection;
import java.util.Map;

public interface ProductService {

    Page<ProductItemResponseDto> getPageOfProducts(ProductFilterRequestDto productFilterRequestDto, Pageable pageable);

    ProductInfoResponseDto getProductInfoById(Long productId);

    ProductVariantResponseDto getProductVariantById(Long variantId);

    /**
     * Получение информации о продуктах по списку ID (batch операция)
     */
    Map<Long, ProductInfoResponseDto> getProductInfoByIds(Collection<Long> productIds);
}
