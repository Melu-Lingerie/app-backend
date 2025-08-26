package ru.mellingerie.products.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mellingerie.products.domain.Product;
import ru.mellingerie.products.dto.ProductInfoDto;
import ru.mellingerie.products.repository.ProductRepository;
import ru.mellingerie.products.service.ProductService;
import ru.mellingerie.products.dto.request.ProductFilterRequestDto;
import ru.mellingerie.products.dto.response.ProductItemResponseDto;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ProductVariantService productVariantService;

    @Override
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
    public ProductInfoDto getProductInfo(Long productId, String color) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Product was not found by given externalId = %s", productId)));



        product.getReviews();

        return new ProductInfoDto(
                product.getName(),
                product.getArticleNumber(),
                product.getBasePrice(),
                availableColorsForEachProducts.values(),
                availableSizesForEachProducts.values(),
                /*collectionId*/,
                /*categoryId*/,
                product.getDescription(),
                product.getMaterial(),
                /*sizeOnModel*/,
                /*review*/
        );
    }
}
