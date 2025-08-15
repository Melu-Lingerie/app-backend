package ru.mellingerie.products.service;

import ru.mellingerie.products.dto.ProductFilterDTO;
import ru.mellingerie.products.exception.InvalidFilterException;

public interface ProductFilterValidationService {
    
    /**
     * Валидировать параметры фильтрации
     * 
     * @param filter параметры фильтрации
     * @throws InvalidFilterException если параметры некорректны
     */
    void validateFilter(ProductFilterDTO filter);
    
    /**
     * Валидировать поисковый запрос
     * 
     * @param query поисковый запрос
     * @throws InvalidFilterException если запрос некорректен
     */
    void validateSearchQuery(String query);
    
    /**
     * Валидировать параметры пагинации
     * 
     * @param limit максимальное количество элементов
     * @throws InvalidFilterException если параметры некорректны
     */
    void validatePagination(Integer limit);
} 