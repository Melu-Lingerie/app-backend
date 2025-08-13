package ru.mellingerie.products.dto;

/**
 * Рекомендация стилиста для товара
 */
public record StylingRecommendationDTO(
    Long id,
    String title,
    String description,
    String stylistName,
    String stylistAvatar
) {} 