package ru.melulingerie.products.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.products.domain.Product;
import ru.melulingerie.products.domain.ProductVariant;
import ru.melulingerie.products.dto.ProductInfoResponseDto;
import ru.melulingerie.products.dto.ProductVariantResponseDto;
import ru.melulingerie.products.projection.ProductIdCategoryIdProjection;
import ru.melulingerie.products.repository.ProductRepository;
import ru.melulingerie.products.service.ProductService;
import ru.melulingerie.products.service.ProductVariantService;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantService productVariantService;

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
    public Map<Long, Set<String>> findAvailableColorsByProductIds(Collection<Long> productIds) {
        return productVariantService.findAvailableColorsForEachProducts(productIds);
    }

    @Override
    public Map<Long/*productId*/, Long/*categoryId*/> getCategoryIdByProductIds(List<Long> productIds) {
        return productRepository.findCategoryIdByProductIds(productIds)
                .stream()
                .collect(Collectors.toMap(
                        ProductIdCategoryIdProjection::getProductId,
                        ProductIdCategoryIdProjection::getCategoryId)
                );
    }


}
