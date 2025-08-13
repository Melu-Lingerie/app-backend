package ru.mellingerie.products.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Параметры фильтрации для каталога товаров
 */
public record ProductFilterDTO(
    List<String> categories,
    List<String> colors,
    List<String> sizes,
    BigDecimal minPrice,
    BigDecimal maxPrice,
    Boolean inStock,
    String searchTerm
) {} 