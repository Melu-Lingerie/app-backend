package ru.mellingerie.products.service;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import ru.mellingerie.products.dto.request.ProductFilterRequestDto;
import ru.mellingerie.products.dto.response.ProductItemResponseDto;

public interface ProductService {

    @Transactional(readOnly = true)
    Page<ProductItemResponseDto> getPageOfProducts(ProductFilterRequestDto productFilterRequestDto);
}
