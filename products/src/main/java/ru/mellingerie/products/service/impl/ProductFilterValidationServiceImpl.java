package ru.mellingerie.products.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mellingerie.products.dto.ProductFilterDTO;
import ru.mellingerie.products.exception.InvalidFilterException;
import ru.mellingerie.products.service.ProductFilterValidationService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class ProductFilterValidationServiceImpl implements ProductFilterValidationService {
    
    // Захардкоженные значения фильтров согласно ML-002
    private static final Set<String> ALLOWED_CATEGORIES = Set.of("women", "men", "accessories", "art");
    private static final Set<String> ALLOWED_COLORS = Set.of("red", "blue", "green", "black", "white", "gray", "brown");
    private static final Set<String> ALLOWED_SIZES = Set.of("XS", "S", "M", "L", "XL", "XXL");
    
    private static final BigDecimal MIN_PRICE = BigDecimal.ZERO;
    private static final BigDecimal MAX_PRICE = new BigDecimal("500000");
    private static final int MAX_LIMIT = 50;
    private static final int MIN_SEARCH_LENGTH = 2;
    private static final int MAX_SEARCH_LENGTH = 100;
    
    @Override
    public void validateFilter(ProductFilterDTO filter) {
        if (filter == null) {
            return;
        }
        
        // Валидация категорий
        if (filter.categories() != null && !filter.categories().isEmpty()) {
            List<String> invalidCategories = filter.categories().stream()
                    .filter(category -> !ALLOWED_CATEGORIES.contains(category))
                    .toList();
            
            if (!invalidCategories.isEmpty()) {
                throw new InvalidFilterException("categories", 
                    "Invalid categories: " + String.join(", ", invalidCategories));
            }
        }
        
        // Валидация цветов
        if (filter.colors() != null && !filter.colors().isEmpty()) {
            List<String> invalidColors = filter.colors().stream()
                    .filter(color -> !ALLOWED_COLORS.contains(color.toLowerCase()))
                    .toList();
            
            if (!invalidColors.isEmpty()) {
                throw new InvalidFilterException("colors", 
                    "Invalid colors: " + String.join(", ", invalidColors));
            }
        }
        
        // Валидация размеров
        if (filter.sizes() != null && !filter.sizes().isEmpty()) {
            List<String> invalidSizes = filter.sizes().stream()
                    .filter(size -> !ALLOWED_SIZES.contains(size))
                    .toList();
            
            if (!invalidSizes.isEmpty()) {
                throw new InvalidFilterException("sizes", 
                    "Invalid sizes: " + String.join(", ", invalidSizes));
            }
        }
        
        // Валидация ценового диапазона
        if (filter.minPrice() != null && filter.maxPrice() != null) {
            if (filter.minPrice().compareTo(filter.maxPrice()) > 0) {
                throw new InvalidFilterException("price", 
                    "minPrice cannot be greater than maxPrice");
            }
        }
        
        if (filter.minPrice() != null && filter.minPrice().compareTo(MIN_PRICE) < 0) {
            throw new InvalidFilterException("minPrice", 
                "minPrice cannot be less than " + MIN_PRICE);
        }
        
        if (filter.maxPrice() != null && filter.maxPrice().compareTo(MAX_PRICE) > 0) {
            throw new InvalidFilterException("maxPrice", 
                "maxPrice cannot be greater than " + MAX_PRICE);
        }
        
        // Валидация поискового запроса
        if (filter.searchTerm() != null && !filter.searchTerm().trim().isEmpty()) {
            validateSearchQuery(filter.searchTerm());
        }
    }
    
    @Override
    public void validateSearchQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new InvalidFilterException("query", "Search query cannot be empty");
        }
        
        String trimmedQuery = query.trim();
        if (trimmedQuery.length() < MIN_SEARCH_LENGTH) {
            throw new InvalidFilterException("query", 
                "Search query must be at least " + MIN_SEARCH_LENGTH + " characters long");
        }
        
        if (trimmedQuery.length() > MAX_SEARCH_LENGTH) {
            throw new InvalidFilterException("query", 
                "Search query cannot exceed " + MAX_SEARCH_LENGTH + " characters");
        }
    }
    
    @Override
    public void validatePagination(Integer limit) {
        if (limit == null) {
            throw new InvalidFilterException("limit", "Limit cannot be null");
        }
        
        if (limit <= 0) {
            throw new InvalidFilterException("limit", "Limit must be positive");
        }
        
        if (limit > MAX_LIMIT) {
            throw new InvalidFilterException("limit", 
                "Limit cannot exceed " + MAX_LIMIT);
        }
    }
} 