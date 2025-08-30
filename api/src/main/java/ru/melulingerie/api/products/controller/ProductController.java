package ru.melulingerie.api.products.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;
import ru.melulingerie.api.products.resource.ProductResource;
import ru.melulingerie.facade.products.dto.response.ProductCardResponseDto;
import ru.melulingerie.facade.products.service.ProductFacadeService;
import ru.melulingerie.facade.products.dto.request.ProductCatalogRequestDto;
import ru.melulingerie.facade.products.dto.response.ProductCatalogResponseDto;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductResource {

    private final ProductFacadeService productFacadeService;

    @Override
    public Page<ProductCatalogResponseDto> getCatalog(@Valid ProductCatalogRequestDto productCatalogRequestDto) {
        return productFacadeService.getPageOfProducts(productCatalogRequestDto);
    }

    @Override
    public ProductCardResponseDto getProductCardInfo(@NotNull Long productId) {
        return productFacadeService.getProductCardInfo(productId);
    }
}
