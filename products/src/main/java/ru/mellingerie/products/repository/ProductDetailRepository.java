package ru.mellingerie.products.repository;

import ru.mellingerie.products.dto.ProductDetailDTO;

import java.util.Optional;

public interface ProductDetailRepository {
    
    /**
     * Найти детальную информацию о товаре по ID
     * 
     * @param productId идентификатор товара
     * @param includeReviews включать ли отзывы
     * @param includeRecommendations включать ли рекомендации стилистов
     * @return детальная информация о товаре
     */
    Optional<ProductDetailDTO> findDetailById(Long productId, Boolean includeReviews, Boolean includeRecommendations);
    
    /**
     * Найти детальную информацию о товаре по slug
     * 
     * @param slug slug товара
     * @param includeReviews включать ли отзывы
     * @param includeRecommendations включать ли рекомендации стилистов
     * @return детальная информация о товаре
     */
    Optional<ProductDetailDTO> findDetailBySlug(String slug, Boolean includeReviews, Boolean includeRecommendations);
} 