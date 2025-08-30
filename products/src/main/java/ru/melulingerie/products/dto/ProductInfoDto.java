package ru.melulingerie.products.dto;

import ru.melulingerie.products.domain.Product;

import java.math.BigDecimal;
import java.util.List;

public record ProductInfoDto(
        Long productId,
        String name,
        String articleNumber,
        BigDecimal price,
        String description,
        String structure,
        Float score,
        List<ProductVariantDto> productVariants
) {
    
    public ProductInfoDto (Product product){
        this(
                product.getId(), 
                product.getName(),
                product.getArticleNumber(),
                product.getBasePrice(),
                product.getDescription(), 
                product.getMaterial(),
                product.getScore(),
                product.getVariants().stream().map(ProductVariantDto::new).toList()
        );
    }
}
