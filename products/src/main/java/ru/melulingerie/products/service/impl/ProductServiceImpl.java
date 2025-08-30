package ru.melulingerie.products.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.products.domain.Product;
import ru.melulingerie.products.dto.ProductInfoDto;
import ru.melulingerie.products.repository.ProductRepository;
import ru.melulingerie.products.service.ProductService;
import ru.melulingerie.products.dto.request.ProductFilterRequestDto;
import ru.melulingerie.products.dto.response.ProductItemResponseDto;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ProductVariantService productVariantService;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductItemResponseDto> getPageOfProducts(ProductFilterRequestDto productFilterRequestDto) {
        Page<Product> productsByParams = productRepository.findByParams(productFilterRequestDto);
        Set<Long> productIds = productsByParams.getContent().stream().map(Product::getId).collect(Collectors.toSet());
        Map<Long, Set<String>> availableColorsForEachProducts = productVariantService.findAvailableColorsForEachProducts(productIds);

        return productsByParams.map(entity ->
                new ProductItemResponseDto(
                        entity.getId(),
                        entity.getBasePrice(),
                        entity.getName(),
                        entity.getMainMediaId(),
                        availableColorsForEachProducts.get(entity.getId())
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ProductInfoDto getProductInfo(Long productId) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Product was not found by given externalId = %s", productId)));

        return new ProductInfoDto(product);
    }
}
