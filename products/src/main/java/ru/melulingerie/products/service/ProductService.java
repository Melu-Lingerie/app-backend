package ru.melulingerie.products.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.melulingerie.products.domain.Product;
import ru.melulingerie.products.domain.ProductVariant;
import ru.melulingerie.products.dto.ProductInfoDto;
import ru.melulingerie.products.dto.request.ProductFilterRequestDto;
import ru.melulingerie.products.dto.response.ProductItemResponseDto;

import java.util.Optional;

public interface ProductService {

    Page<ProductItemResponseDto> getPageOfProducts(ProductFilterRequestDto productFilterRequestDto, Pageable pageable);

    ProductInfoDto getProductInfo(Long productId);

    Optional<Product> getProductById(Long productId);

    Optional<ProductVariant> getProductVariantById(Long variantId);
}
