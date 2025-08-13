package ru.mellingerie.products.service;

import java.util.Base64;
import java.util.Objects;

public interface CursorService {
    
    /**
     * Декодировать курсор в данные для пагинации
     * 
     * @param cursor закодированный курсор (base64)
     * @return данные курсора или null если курсор пустой
     */
    CursorData decode(String cursor);
    
    /**
     * Закодировать данные курсора
     * 
     * @param cursorData данные курсора
     * @return закодированный курсор (base64)
     */
    String encode(CursorData cursorData);
    
    /**
     * Создать начальный курсор
     * 
     * @return начальные данные курсора
     */
    CursorData initial();
    
    /**
     * Данные курсора для пагинации
     */
    record CursorData(Integer sortIndex, Long createdAt, Long productId) {
        public CursorData {
            Objects.requireNonNull(sortIndex, "sortIndex cannot be null");
            Objects.requireNonNull(createdAt, "createdAt cannot be null");
            Objects.requireNonNull(productId, "productId cannot be null");
        }
    }
} 