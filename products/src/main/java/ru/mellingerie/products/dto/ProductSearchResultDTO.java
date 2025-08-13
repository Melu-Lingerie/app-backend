package ru.mellingerie.products.dto;

import java.math.BigDecimal;

/**
 * Результат поиска товара
 */
public record ProductSearchResultDTO(
    Long id,
    String name,
    String slug,
    BigDecimal currentPrice,
    String categoryName,
    String categorySlug,
    String mainImage,
    Float relevanceScore
) {} 