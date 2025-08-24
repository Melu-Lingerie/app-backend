package ru.melulingerie.facade.products.service;

import org.springframework.data.domain.Page;
import ru.melulingerie.facade.products.dto.ProductCatalogRequestDto;
import ru.melulingerie.facade.products.dto.ProductCatalogResponseDto;

public interface ProductFacadeService {
    Page<ProductCatalogResponseDto> getPageOfProducts(ProductCatalogRequestDto productCatalogRequestDto);
}
