package ru.melulingerie.products.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.melulingerie.products.dto.ProductInfoDto;
import ru.melulingerie.products.dto.ProductVariantDto;
import ru.melulingerie.products.dto.request.ProductFilterRequestDto;
import ru.melulingerie.products.dto.response.ProductItemResponseDto;

public interface ProductService {

    Page<ProductItemResponseDto> getPageOfProducts(ProductFilterRequestDto productFilterRequestDto, Pageable pageable);

    ProductInfoDto getProductInfoById(Long productId);

    ProductVariantDto getProductVariantById(Long variantId);
}
