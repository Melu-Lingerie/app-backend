package ru.mellingerie.products.service;

import ru.mellingerie.products.dto.CatalogResponseDTO;
import ru.mellingerie.products.dto.ProductFilterDTO;

public interface ProductQueryService {
    
    /**
     * Получить каталог товаров с фильтрацией и курсорной пагинацией
     * 
     * @param filter параметры фильтрации
     * @param cursor курсор для пагинации (base64)
     * @param limit максимальное количество товаров (≤50)
     * @return каталог товаров с информацией о пагинации
     */
    CatalogResponseDTO getCatalog(ProductFilterDTO filter, String cursor, Integer limit);
} 