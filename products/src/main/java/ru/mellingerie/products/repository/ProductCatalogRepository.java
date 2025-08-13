package ru.mellingerie.products.repository;

import ru.mellingerie.products.dto.PublicProductDTO;
import ru.mellingerie.products.service.CursorService;

import java.util.List;

public interface ProductCatalogRepository {
    
    /**
     * Найти товары для каталога с фильтрацией и курсорной пагинацией
     * 
     * @param cursorData данные курсора
     * @param limit максимальное количество товаров
     * @param categories список категорий для фильтрации
     * @param colors список цветов для фильтрации
     * @param sizes список размеров для фильтрации
     * @param minPrice минимальная цена
     * @param maxPrice максимальная цена
     * @param inStock только товары в наличии
     * @return список товаров для каталога
     */
    List<PublicProductDTO> findPublicProducts(
            CursorService.CursorData cursorData,
            Integer limit,
            List<String> categories,
            List<String> colors,
            List<String> sizes,
            Double minPrice,
            Double maxPrice,
            Boolean inStock
    );
    
    /**
     * Подсчитать общее количество видимых товаров
     * 
     * @param categories список категорий для фильтрации
     * @param colors список цветов для фильтрации
     * @param sizes список размеров для фильтрации
     * @param minPrice минимальная цена
     * @param maxPrice максимальная цена
     * @param inStock только товары в наличии
     * @return общее количество товаров
     */
    Long countPublicProducts(
            List<String> categories,
            List<String> colors,
            List<String> sizes,
            Double minPrice,
            Double maxPrice,
            Boolean inStock
    );
} 