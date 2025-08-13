package ru.mellingerie.products.service;

import ru.mellingerie.products.dto.ProductFilterDTO;
import ru.mellingerie.products.dto.SearchResponseDTO;

public interface ProductSearchService {
    
    /**
     * Поиск товаров по запросу с фильтрацией и пагинацией
     * 
     * @param query поисковый запрос (минимум 2 символа)
     * @param filter параметры фильтрации
     * @param page номер страницы (начиная с 0)
     * @param limit максимальное количество товаров (≤50)
     * @return результаты поиска с метаданными
     */
    SearchResponseDTO searchProducts(String query, ProductFilterDTO filter, Integer page, Integer limit);
} 