package ru.melulingerie.facade.products.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.melulingerie.facade.media.dto.MediaGetInfoFacadeResponseDto;
import ru.melulingerie.facade.media.service.MediaGetFacadeService;
import ru.melulingerie.facade.products.dto.request.ProductCatalogRequestDto;
import ru.melulingerie.facade.products.dto.response.ProductCardResponseDto;
import ru.melulingerie.facade.products.dto.response.ProductCatalogResponseDto;
import ru.melulingerie.facade.products.mapper.ProductMapper;
import ru.melulingerie.facade.products.service.ProductFacadeService;
import ru.melulingerie.products.dto.ProductInfoDto;
import ru.melulingerie.products.dto.request.ProductFilterRequestDto;
import ru.melulingerie.products.dto.response.ProductItemResponseDto;
import ru.melulingerie.products.service.ProductService;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductFacadeServiceImpl implements ProductFacadeService {

    private final ProductMapper productMapper;
    private final ProductService productService;
    private final MediaGetFacadeService mediaGetFacadeService;

    @Override
    public Page<ProductCatalogResponseDto> getPageOfProducts(ProductCatalogRequestDto request, Pageable pageable) {
        ProductFilterRequestDto productFilterRequestDto = productMapper.toProductFilterRequestDto(request);
        Page<ProductItemResponseDto> pageOfProducts = productService.getPageOfProducts(productFilterRequestDto, pageable);

        Map<Long/*productId*/,Long/*mediaId*/> mainMediaIds = pageOfProducts.getContent()
                .stream()
                .collect(Collectors.toMap(
                        ProductItemResponseDto::productId,
                        ProductItemResponseDto::mainMediaId
                ));

        Map<Long, MediaGetInfoFacadeResponseDto> mediaByIds = mediaGetFacadeService.getMediaByIds(mainMediaIds.values());

        return pageOfProducts.map(response ->
                new ProductCatalogResponseDto(
                        response.productId(),
                        response.name(),
                        response.price(),
                        mediaByIds.get(mainMediaIds.get(response.productId())).s3Url()
                        )
        );
    }

    @Override
    public ProductCardResponseDto getProductCardInfo(Long productId) {
        ProductInfoDto productInfoDto = productService.getProductInfoById(productId);
        //todo поход в медиа сервис за ссылками
        Map<Long/*mediaId*/, String/*url*/> mediaInfo = new HashMap<>();
        return new ProductCardResponseDto(productInfoDto, mediaInfo);
    }
}
