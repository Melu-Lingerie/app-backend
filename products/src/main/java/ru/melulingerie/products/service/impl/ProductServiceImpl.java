package ru.melulingerie.products.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.products.domain.Product;
import ru.melulingerie.products.domain.ProductVariant;
import ru.melulingerie.products.dto.ProductInfoResponseDto;
import ru.melulingerie.products.dto.ProductVariantResponseDto;
import ru.melulingerie.products.repository.ProductRepository;
import ru.melulingerie.products.service.ProductService;
import ru.melulingerie.products.dto.request.ProductFilterRequestDto;
import ru.melulingerie.products.dto.response.ProductItemResponseDto;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantService productVariantService;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductItemResponseDto> getPageOfProducts(ProductFilterRequestDto productFilterRequestDto, Pageable pageable) {
        Page<Product> productsByParams = productRepository.findByParams(productFilterRequestDto, pageable);
        Set<Long> productIds = productsByParams.getContent().stream().map(Product::getId).collect(Collectors.toSet());
        Map<Long, Set<String>> availableColorsForEachProducts = productVariantService.findAvailableColorsForEachProducts(productIds);
        Map<Long, Set<Long>> availablePricesForEachProducts = productVariantService.findAvailablePricesForEachProducts(productIds);

        return productsByParams.map(entity ->
                new ProductItemResponseDto(
                        entity.getId(),
                        availablePricesForEachProducts.get(entity.getId()),
                        entity.getName(),
                        entity.getMainMediaId(),
                        availableColorsForEachProducts.get(entity.getId())
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ProductInfoResponseDto getProductInfoById(Long productId) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Product was not found by given externalId = %s", productId)));

        return new ProductInfoResponseDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductVariantResponseDto getProductVariantById(Long variantId) {
        ProductVariant productVariant = productVariantService.getVariantById(variantId);
        return new ProductVariantResponseDto(productVariant);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, ProductInfoResponseDto> getProductInfoByIds(Collection<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Product> products = productRepository.findAllById(productIds);
        return products.stream()
                .collect(Collectors.toMap(
                    Product::getId,
                    product -> new ProductInfoResponseDto(product)
                ));
    }
}
