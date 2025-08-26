package ru.mellingerie.api.products.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;
import ru.mellingerie.api.products.resource.ProductResource;
import ru.melulingerie.facade.products.dto.response.ProductCardResponseDto;
import ru.melulingerie.facade.products.service.ProductFacadeService;
import ru.melulingerie.facade.products.dto.request.ProductCatalogRequestDto;
import ru.melulingerie.facade.products.dto.response.ProductCatalogResponseDto;

import java.math.BigDecimal;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProductController implements ProductResource {

    private final ProductFacadeService productFacadeService;

    @Override
    public Page<ProductCatalogResponseDto> getCatalog(BigDecimal minPrice,
                                                      BigDecimal maxPrice,
                                                      Set<Long> categories,
                                                      Set<String> sizes,
                                                      Set<String> sizesOfCups,
                                                      Set<String> colors,
                                                      Pageable pageable) {
        log.info("Получен запрос на просмотр каталога");

        ProductCatalogRequestDto productCatalogRequestDto = ProductCatalogRequestDto.builder()
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .categories(categories)
                .colors(colors)
                .sizes(sizes)
                .sizesOfBraWithCups(sizesOfCups)
                .pageable(pageable)
                .build();

        return productFacadeService.getPageOfProducts(productCatalogRequestDto);
    }

    @Override
    public ProductCardResponseDto getProductCardInfo(Long productId) {
        return productFacadeService.getProductCardInfo(productId);
    }
}
