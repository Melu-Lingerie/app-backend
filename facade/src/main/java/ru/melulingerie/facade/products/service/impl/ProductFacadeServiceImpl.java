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
import ru.melulingerie.facade.products.dto.response.ProductVariantCardDto;
import ru.melulingerie.facade.products.dto.response.ProductVariantMediaCardDto;
import ru.melulingerie.facade.products.mapper.ProductMapper;
import ru.melulingerie.facade.products.service.ProductFacadeService;
import ru.melulingerie.price.dto.response.PriceQuoteDto;
import ru.melulingerie.price.service.PriceService;
import ru.melulingerie.products.dto.ProductInfoResponseDto;
import ru.melulingerie.products.dto.ProductVariantMediaResponseDto;
import ru.melulingerie.products.dto.ProductVariantResponseDto;
import ru.melulingerie.products.enums.ProductStatus;
import ru.melulingerie.products.service.ProductService;
import ru.melulingerie.query.dto.request.ProductCatalogFilterRequestDto;
import ru.melulingerie.query.dto.response.ProductCatalogItemResponseDto;
import ru.melulingerie.query.service.ProductCatalogQueryService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductFacadeServiceImpl implements ProductFacadeService {

    private final PriceService priceService;
    private final ProductMapper productMapper;
    private final ProductService productService;
    private final MediaGetFacadeService mediaGetFacadeService;
    private final ProductCatalogQueryService productCatalogQueryService;

    @Override
    public Page<ProductCatalogResponseDto> getPageOfProducts(ProductCatalogRequestDto request, Pageable pageable) {

        ProductCatalogFilterRequestDto productFilterRequestDto = productMapper.toProductCatalogFilterRequestDto(request);
        Page<ProductCatalogItemResponseDto> pageOfProducts = productCatalogQueryService.getProductCatalogItems(productFilterRequestDto, pageable);

        Set<Long> productIds = pageOfProducts.getContent()
                .stream()
                .map(ProductCatalogItemResponseDto::productId)
                .collect(Collectors.toSet());
        Map<Long, Set<String>> availableColorsByProductIds = productService.findAvailableColorsByProductIds(productIds);

        return pageOfProducts.map(item ->
                new ProductCatalogResponseDto(
                        item.productId(),
                        item.name(),
                        item.price(),
                        item.s3url(),
                        availableColorsByProductIds.get(item.productId()),
                        ProductStatus.valueOf(item.productStatus())
                )
        );
    }

    @Override
    public ProductCardResponseDto getProductCardInfo(Long productId) {
        ProductInfoResponseDto productInfoResponseDto = productService.getProductInfoById(productId);

        Set<Long> mediaIds = new HashSet<>();
        Set<Long> priceIds = new HashSet<>();

        for (ProductVariantResponseDto pv : productInfoResponseDto.productVariants()) {
            priceIds.add(pv.priceId());
            for (ProductVariantMediaResponseDto pvm : pv.productVariantMedia()) {
                mediaIds.add(pvm.mediaId());
            }
        }

        Map<Long/*mediaId*/, MediaGetInfoFacadeResponseDto> mediaByIds = mediaGetFacadeService.getMediaByIds(mediaIds);
        Map<Long/*priceId*/, PriceQuoteDto> currentPrices = priceService.getPricesByIds(priceIds);

        List<ProductVariantCardDto> productVariantCardDtos = new ArrayList<>();
        for (ProductVariantResponseDto productVariant : productInfoResponseDto.productVariants()) {
            List<ProductVariantMediaCardDto> productVariantMediaCardDtos = new ArrayList<>(productVariant.productVariantMedia().size());
            for (ProductVariantMediaResponseDto pvm : productVariant.productVariantMedia()) {
                MediaGetInfoFacadeResponseDto mediaGetInfo = mediaByIds.get(pvm.mediaId());
                productVariantMediaCardDtos.add(new ProductVariantMediaCardDto(
                        mediaGetInfo.id(),
                        pvm.sortOrder(),
                        mediaGetInfo.s3Url()
                ));
            }

            productVariantCardDtos.add(new ProductVariantCardDto(
                    productVariant.id(),
                    productVariant.colorName(),
                    productVariant.size(),
                    productVariant.stockQuantity(),
                    currentPrices.get(productVariant.priceId()).price(),
                    productVariant.isAvailable(),
                    productVariant.sortOrder(),
                    productVariantMediaCardDtos
            ));
        }

        return new ProductCardResponseDto(
                productInfoResponseDto.productId(),
                productInfoResponseDto.name(),
                productInfoResponseDto.articleNumber(),
                productInfoResponseDto.description(),
                productInfoResponseDto.structure(),
                productInfoResponseDto.score(),
                productVariantCardDtos
        );
    }
}
