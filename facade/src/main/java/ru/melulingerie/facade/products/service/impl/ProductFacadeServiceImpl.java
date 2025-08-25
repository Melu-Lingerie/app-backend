package ru.melulingerie.facade.products.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.mellingerie.media.api.MediaApi;
import ru.mellingerie.products.dto.request.ProductFilterRequestDto;
import ru.mellingerie.products.dto.response.ProductItemResponseDto;
import ru.mellingerie.products.service.ProductService;
import ru.melulingerie.facade.products.dto.ProductCatalogRequestDto;
import ru.melulingerie.facade.products.dto.ProductCatalogResponseDto;
import ru.melulingerie.facade.products.mapper.ProductMapper;
import ru.melulingerie.facade.products.service.ProductFacadeService;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductFacadeServiceImpl implements ProductFacadeService {

    private final ProductMapper productMapper;
    private final ProductService productService;

    @Override
    public Page<ProductCatalogResponseDto> getPageOfProducts(ProductCatalogRequestDto request) {

        ProductFilterRequestDto productFilterRequestDto = productMapper.toProductFilterRequestDto(request);

        Page<ProductItemResponseDto> pageOfProducts = productService.getPageOfProducts(productFilterRequestDto);

        Map<Long/*productId*/,Long/*mediaId*/> mainMediaIds = pageOfProducts.getContent()
                .stream()
                .collect(Collectors.toMap(
                        ProductItemResponseDto::productId,
                        ProductItemResponseDto::mainMediaId
                ));

        //todo добавить поход в mediaService за фотографиями
        //todo Map<Long/*mediaId*/, MediaInfo> mediaInfoById = mediaService.getMediaByIds(mainMediaIds.values());

        //временно для проверки работоспособности
        return pageOfProducts.map(response ->
                new ProductCatalogResponseDto(
                        response.productId(),
                        response.name(),
                        response.price()
                        //todo mediaInfoById.get(response.mainMediaId())
                        )
        );
    }
}
