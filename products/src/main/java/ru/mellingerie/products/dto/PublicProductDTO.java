package ru.mellingerie.products.dto;

import java.math.BigDecimal;

/**
 * Публичное представление товара для каталога
 * Исключенные поля: sku, costPrice, supplierId, stockQuantity
 */
public record PublicProductDTO(
    Long id,
    String name,
    String slug,
    BigDecimal currentPrice,
    BigDecimal basePrice,
    String categoryName,
    String categorySlug,
    String mainImage,
    String imageAlt,
    Integer availableVariants
) {} 