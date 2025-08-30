package ru.melulingerie.products.service;

import org.springframework.data.domain.Page;
import ru.melulingerie.products.dto.ProductInfoDto;
import ru.melulingerie.products.dto.request.ProductFilterRequestDto;
import ru.melulingerie.products.dto.response.ProductItemResponseDto;

public interface ProductService {

    Page<ProductItemResponseDto> getPageOfProducts(ProductFilterRequestDto productFilterRequestDto);

    ProductInfoDto getProductInfo(Long productId);
}
