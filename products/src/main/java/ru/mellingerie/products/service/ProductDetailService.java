package ru.mellingerie.products.service;

import ru.mellingerie.products.dto.ProductDetailDTO;

public interface ProductDetailService {
    
    /**
     * Получить детальную информацию о товаре по ID
     * 
     * @param productId идентификатор товара
     * @param includeReviews включать ли отзывы
     * @param includeRecommendations включать ли рекомендации стилистов
     * @return детальная информация о товаре
     */
    ProductDetailDTO getProductDetail(Long productId, Boolean includeReviews, Boolean includeRecommendations);
    
    /**
     * Получить детальную информацию о товаре по slug
     * 
     * @param slug slug товара
     * @param includeReviews включать ли отзывы
     * @param includeRecommendations включать ли рекомендации стилистов
     * @return детальная информация о товаре
     */
    ProductDetailDTO getProductDetailBySlug(String slug, Boolean includeReviews, Boolean includeRecommendations);
} 