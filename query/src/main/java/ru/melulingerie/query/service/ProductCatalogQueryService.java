package ru.melulingerie.query.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.melulingerie.query.dto.request.ProductCatalogFilterRequestDto;
import ru.melulingerie.query.dto.response.ProductCatalogItemResponseDto;

public interface ProductCatalogQueryService {
    Page<ProductCatalogItemResponseDto> getProductCatalogItems(ProductCatalogFilterRequestDto req, Pageable pageable);
}
