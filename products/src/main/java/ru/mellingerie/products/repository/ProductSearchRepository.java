package ru.mellingerie.products.repository;

import ru.mellingerie.products.dto.ProductSearchResultDTO;

import java.util.List;

public interface ProductSearchRepository {
    
    /**
     * Поиск товаров по запросу с фильтрацией
     * 
     * @param query нормализованный поисковый запрос
     * @param categories список категорий для фильтрации
     * @param minPrice минимальная цена
     * @param maxPrice максимальная цена
     * @param offset смещение для пагинации
     * @param limit максимальное количество товаров
     * @return список найденных товаров
     */
    List<ProductSearchResultDTO> searchProducts(
            String query,
            List<String> categories,
            Double minPrice,
            Double maxPrice,
            Integer offset,
            Integer limit
    );
    
    /**
     * Подсчитать общее количество результатов поиска
     * 
     * @param query нормализованный поисковый запрос
     * @param categories список категорий для фильтрации
     * @param minPrice минимальная цена
     * @param maxPrice максимальная цена
     * @return общее количество найденных товаров
     */
    Long countSearchResults(
            String query,
            List<String> categories,
            Double minPrice,
            Double maxPrice
    );
} 