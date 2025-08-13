package ru.mellingerie.products.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Детальная информация о товаре
 */
public record ProductDetailDTO(
    Long id,
    String name,
    String slug,
    String description,
    BigDecimal basePrice,
    BigDecimal currentPrice,
    String material,
    String careInstructions,
    LocalDateTime createdAt,
    
    // Категория
    Long categoryId,
    String categoryName,
    String categorySlug,
    
    // Медиа контент
    List<MediaDTO> media,
    
    // Варианты товара
    List<VariantDTO> variants,
    
    // Отзывы (опционально)
    ReviewsSummaryDTO reviewsSummary,
    
    // Рекомендации стилистов (опционально)
    List<StylingRecommendationDTO> stylingRecommendations,
    
    // Хлебные крошки для навигации
    List<BreadcrumbDTO> breadcrumbs
) {} 