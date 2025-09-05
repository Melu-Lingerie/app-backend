package ru.melulingerie.facade.products.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.melulingerie.facade.products.dto.response.ProductCardResponseDto;
import ru.melulingerie.facade.products.dto.request.ProductCatalogRequestDto;
import ru.melulingerie.facade.products.dto.response.ProductCatalogResponseDto;

public interface ProductFacadeService {
    Page<ProductCatalogResponseDto> getPageOfProducts(ProductCatalogRequestDto productCatalogRequestDto, Pageable pageable);

    ProductCardResponseDto getProductCardInfo(Long productId);
}
