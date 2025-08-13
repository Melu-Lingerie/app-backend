package ru.mellingerie.products.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ru.mellingerie.products.dto.ProductFilterDTO;
import ru.mellingerie.products.dto.ProductSearchResultDTO;
import ru.mellingerie.products.dto.SearchMetaDTO;
import ru.mellingerie.products.dto.SearchResponseDTO;
import ru.mellingerie.products.repository.ProductSearchRepository;
import ru.mellingerie.products.service.ProductFilterValidationService;
import ru.mellingerie.products.service.ProductSearchService;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchServiceImpl implements ProductSearchService {
    
    private final ProductSearchRepository searchRepository;
    private final ProductFilterValidationService filterValidationService;
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public SearchResponseDTO searchProducts(String query, ProductFilterDTO filter, Integer page, Integer limit) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Валидация параметров
            filterValidationService.validateSearchQuery(query);
            filterValidationService.validateFilter(filter);
            filterValidationService.validatePagination(limit);
            
            if (page == null || page < 0) {
                page = 0;
            }
            
            // Нормализация запроса
            String normalizedQuery = normalizeSearchQuery(query);
            
            // Построение кэш-ключа
            String cacheKey = buildSearchCacheKey(normalizedQuery, filter, page, limit);
            
            // Попытка получить из кэша
            SearchResponseDTO cached = (SearchResponseDTO) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.debug("Cache hit for search request: {}", cacheKey);
                return cached;
            }
            
            // Подготовка параметров для репозитория
            List<String> categories = filter != null ? filter.categories() : null;
            Double minPrice = filter != null && filter.minPrice() != null ? 
                filter.minPrice().doubleValue() : null;
            Double maxPrice = filter != null && filter.maxPrice() != null ? 
                filter.maxPrice().doubleValue() : null;
            Integer offset = page * limit;
            
            // Выполнение поиска
            List<ProductSearchResultDTO> searchResults = searchRepository.searchProducts(
                normalizedQuery, categories, minPrice, maxPrice, offset, limit
            );
            
            // Подсчет общего количества результатов
            Long totalFound = searchRepository.countSearchResults(
                normalizedQuery, categories, minPrice, maxPrice
            );
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Построение метаданных поиска
            SearchMetaDTO searchMeta = new SearchMetaDTO(normalizedQuery, executionTime);
            
            // Построение ответа
            SearchResponseDTO response = new SearchResponseDTO(
                searchResults,
                totalFound,
                (page + 1) * limit < totalFound,
                page,
                searchMeta
            );
            
            // Сохранение в кэш (только если есть результаты)
            if (!searchResults.isEmpty()) {
                redisTemplate.opsForValue().set(cacheKey, response, Duration.ofSeconds(180));
                log.debug("Cached search response for key: {}", cacheKey);
            }
            
            log.info("Search request completed in {}ms, found {} products for query: '{}'", 
                executionTime, searchResults.size(), normalizedQuery);
            
            return response;
            
        } catch (Exception e) {
            log.error("Error during search request for query: '{}'", query, e);
            throw e;
        }
    }
    
    private String normalizeSearchQuery(String query) {
        if (query == null) {
            return "";
        }
        
        return query.trim()
            .toLowerCase()
            .replaceAll("\\s+", " ") // Заменяем множественные пробелы на один
            .replaceAll("[^а-яёa-z0-9\\s]", ""); // Убираем специальные символы
    }
    
    private String buildSearchCacheKey(String query, ProductFilterDTO filter, Integer page, Integer limit) {
        StringBuilder keyBuilder = new StringBuilder("search:");
        
        // Добавляем хэш запроса
        keyBuilder.append("q:").append(query.hashCode()).append(":");
        
        // Добавляем фильтры
        if (filter != null) {
            if (filter.categories() != null && !filter.categories().isEmpty()) {
                keyBuilder.append("cat:").append(String.join(",", filter.categories())).append(":");
            }
            if (filter.minPrice() != null) {
                keyBuilder.append("min:").append(filter.minPrice()).append(":");
            }
            if (filter.maxPrice() != null) {
                keyBuilder.append("max:").append(filter.maxPrice()).append(":");
            }
        }
        
        // Добавляем пагинацию
        keyBuilder.append("page:").append(page).append(":");
        keyBuilder.append("limit:").append(limit);
        
        return keyBuilder.toString();
    }
} 