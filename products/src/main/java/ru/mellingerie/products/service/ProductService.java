package ru.mellingerie.products.service;

import org.springframework.data.domain.Page;
import ru.mellingerie.products.dto.request.ProductFilterRequestDto;
import ru.mellingerie.products.dto.response.ProductItemResponseDto;

public interface ProductService {
    Page<ProductItemResponseDto> getPageOfProducts(ProductFilterRequestDto productFilterRequestDto);
}
