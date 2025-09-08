package ru.melulingerie.api.products.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RestController;
import ru.melulingerie.api.products.resource.ProductResource;
import ru.melulingerie.facade.products.dto.request.ProductCatalogRequestDto;
import ru.melulingerie.facade.products.dto.response.ProductCardResponseDto;
import ru.melulingerie.facade.products.dto.response.ProductCatalogResponseDto;
import ru.melulingerie.facade.products.service.ProductFacadeService;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductResource {

    private final ProductFacadeService productFacadeService;

    @Override
    public Page<ProductCatalogResponseDto> getCatalog(@Valid ProductCatalogRequestDto productCatalogRequestDto,
                                                      @PageableDefault(page = 0, size = 20, sort = "createdAt") Pageable pageable) {
        return productFacadeService.getPageOfProducts(productCatalogRequestDto, pageable);
    }

    @Override
    public ProductCardResponseDto getProductCardInfo(Long productId) {
        return productFacadeService.getProductCardInfo(productId);
    }
}
