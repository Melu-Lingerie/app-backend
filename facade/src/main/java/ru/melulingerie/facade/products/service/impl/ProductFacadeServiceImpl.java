package ru.melulingerie.facade.products.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.mellingerie.media.api.MediaApi;
import ru.mellingerie.products.dto.request.ProductFilterRequestDto;
import ru.mellingerie.products.service.ProductService;
import ru.mellingerie.products.dto.response.ProductItemResponseDto;
import ru.melulingerie.facade.products.service.ProductFacadeService;
import ru.melulingerie.facade.products.dto.ProductCatalogRequestDto;
import ru.melulingerie.facade.products.dto.ProductCatalogResponseDto;
import ru.melulingerie.facade.products.mapper.ProductMapper;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductFacadeServiceImpl implements ProductFacadeService {

    private final MediaApi mediaApi;
    private final ProductMapper productMapper;
    private final ProductService productService;

    @Override
    public Page<ProductCatalogResponseDto> getPageOfProducts(ProductCatalogRequestDto request) {

        ProductFilterRequestDto productFilterRequestDto = productMapper.toProductFilterRequestDto(request);

        Page<ProductItemResponseDto> pageOfProducts = productService.getPageOfProducts(productFilterRequestDto);

        Set<Long> productIds = pageOfProducts.getContent().stream()
                .map(ProductItemResponseDto::productId)
                .collect(Collectors.toSet());

        //todo добавить поход в mediaService за фотографиями

        //временно для проверки работоспособности
        return pageOfProducts.map(productMapper::toProductCatalogResponseDto);
    }
}
