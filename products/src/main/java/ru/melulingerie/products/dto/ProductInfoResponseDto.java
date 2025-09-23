package ru.melulingerie.products.dto;

import ru.melulingerie.products.domain.Product;

import java.util.List;

public record ProductInfoResponseDto(
        Long productId,
        String name,
        String articleNumber,
        Long categoryId,
        String description,
        String structure,
        Float score,
        String care,
        List<ProductVariantResponseDto> productVariants
) {
    
    public ProductInfoResponseDto(Product product) {
        this(
                product.getId(), 
                product.getName(),
                product.getArticleNumber(),
                product.getCategory().getId(),
                product.getDescription(),
                product.getMaterial(),
                product.getScore(),
                product.getCareInstructions(),
                product.getVariants().stream().map(ProductVariantResponseDto::new).toList()
        );
    }
}
