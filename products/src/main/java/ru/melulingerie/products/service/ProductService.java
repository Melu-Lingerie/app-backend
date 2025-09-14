package ru.melulingerie.products.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.melulingerie.products.dto.ProductInfoResponseDto;
import ru.melulingerie.products.dto.ProductVariantResponseDto;
import ru.melulingerie.products.dto.request.ProductFilterRequestDto;
import ru.melulingerie.products.dto.response.ProductItemResponseDto;

public interface ProductService {

    Page<ProductItemResponseDto> getPageOfProducts(ProductFilterRequestDto productFilterRequestDto, Pageable pageable);

    ProductInfoResponseDto getProductInfoById(Long productId);

    ProductVariantResponseDto getProductVariantById(Long variantId);
}
