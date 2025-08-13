package ru.mellingerie.products.dto;

/**
 * Сводка отзывов о товаре
 */
public record ReviewsSummaryDTO(
    Integer totalReviews,
    Double avgRating,
    Integer fiveStar,
    Integer fourStar,
    Integer threeStar,
    Integer twoStar,
    Integer oneStar
) {} 