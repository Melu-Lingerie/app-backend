package ru.mellingerie.products.dto;

import java.math.BigDecimal;

/**
 * Вариант товара (цвет, размер, цена)
 */
public record VariantDTO(
    String colorName,
    String colorHex,
    String size,
    Integer stockQuantity,
    BigDecimal additionalPrice,
    BigDecimal variantPrice,
    Boolean isAvailable
) {} 