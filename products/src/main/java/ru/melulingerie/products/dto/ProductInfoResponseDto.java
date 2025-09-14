package ru.melulingerie.products.dto;

import ru.melulingerie.products.domain.Product;

import java.util.List;

public record ProductInfoResponseDto(
        Long productId,
        String name,
        String articleNumber,
        String description,
        String structure,
        Float score,
        List<ProductVariantResponseDto> productVariants
) {
    
    public ProductInfoResponseDto(Product product) {
        this(
                product.getId(), 
                product.getName(),
                product.getArticleNumber(),
                product.getDescription(),
                product.getMaterial(),
                product.getScore(),
                product.getVariants().stream().map(ProductVariantResponseDto::new).toList()
        );
    }
}
