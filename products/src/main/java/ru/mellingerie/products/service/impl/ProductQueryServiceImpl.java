package ru.mellingerie.products.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ru.mellingerie.products.dto.CatalogResponseDTO;
import ru.mellingerie.products.dto.ProductFilterDTO;
import ru.mellingerie.products.dto.PublicProductDTO;
import ru.mellingerie.products.repository.ProductCatalogRepository;
import ru.mellingerie.products.service.CursorService;
import ru.mellingerie.products.service.ProductFilterValidationService;
import ru.mellingerie.products.service.ProductQueryService;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductQueryServiceImpl implements ProductQueryService {
    
    private final ProductCatalogRepository catalogRepository;
    private final ProductFilterValidationService filterValidationService;
    private final CursorService cursorService;
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public CatalogResponseDTO getCatalog(ProductFilterDTO filter, String cursor, Integer limit) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Валидация параметров
            filterValidationService.validateFilter(filter);
            filterValidationService.validatePagination(limit);
            
            // Декодирование курсора
            CursorService.CursorData cursorData = cursor != null ? 
                cursorService.decode(cursor) : 
                cursorService.initial();
            
            // Построение кэш-ключа
            String cacheKey = buildCacheKey(filter, cursor, limit);
            
            // Попытка получить из кэша
            CatalogResponseDTO cached = (CatalogResponseDTO) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.debug("Cache hit for catalog request: {}", cacheKey);
                return cached;
            }
            
            // Подготовка параметров для репозитория
            List<String> categories = filter != null ? filter.categories() : null;
            List<String> colors = filter != null ? filter.colors() : null;
            List<String> sizes = filter != null ? filter.sizes() : null;
            Double minPrice = filter != null && filter.minPrice() != null ? 
                filter.minPrice().doubleValue() : null;
            Double maxPrice = filter != null && filter.maxPrice() != null ? 
                filter.maxPrice().doubleValue() : null;
            Boolean inStock = filter != null ? filter.inStock() : null;
            
            // Выборка из БД
            List<PublicProductDTO> products = catalogRepository.findPublicProducts(
                cursorData, limit, categories, colors, sizes, minPrice, maxPrice, inStock
            );
            
            // Подсчет общего количества
            Long totalVisible = catalogRepository.countPublicProducts(
                categories, colors, sizes, minPrice, maxPrice, inStock
            );
            
            // Построение следующего курсора
            String nextCursor = null;
            Boolean hasMore = false;
            
            if (products.size() == limit) {
                PublicProductDTO lastProduct = products.get(products.size() - 1);
                // Здесь нужно получить sortIndex, createdAt из lastProduct
                // Для упрощения используем заглушку
                CursorService.CursorData nextCursorData = new CursorService.CursorData(
                    lastProduct.id().intValue(), // sortIndex
                    System.currentTimeMillis(), // createdAt
                    lastProduct.id()
                );
                nextCursor = cursorService.encode(nextCursorData);
                hasMore = true;
            }
            
            // Построение ответа
            CatalogResponseDTO response = new CatalogResponseDTO(
                products,
                nextCursor,
                hasMore,
                totalVisible
            );
            
            // Сохранение в кэш (только если есть результаты)
            if (!products.isEmpty()) {
                redisTemplate.opsForValue().set(cacheKey, response, Duration.ofSeconds(120));
                log.debug("Cached catalog response for key: {}", cacheKey);
            }
            
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("Catalog request completed in {}ms, found {} products", executionTime, products.size());
            
            return response;
            
        } catch (Exception e) {
            log.error("Error during catalog request", e);
            throw e;
        }
    }
    
    private String buildCacheKey(ProductFilterDTO filter, String cursor, Integer limit) {
        StringBuilder keyBuilder = new StringBuilder("catalog:");
        
        // Добавляем хэш фильтров
        if (filter != null) {
            keyBuilder.append("filter:");
            if (filter.categories() != null) {
                keyBuilder.append("cat:").append(String.join(",", filter.categories())).append(":");
            }
            if (filter.colors() != null) {
                keyBuilder.append("col:").append(String.join(",", filter.colors())).append(":");
            }
            if (filter.sizes() != null) {
                keyBuilder.append("sz:").append(String.join(",", filter.sizes())).append(":");
            }
            if (filter.minPrice() != null) {
                keyBuilder.append("min:").append(filter.minPrice()).append(":");
            }
            if (filter.maxPrice() != null) {
                keyBuilder.append("max:").append(filter.maxPrice()).append(":");
            }
            if (filter.inStock() != null) {
                keyBuilder.append("stock:").append(filter.inStock()).append(":");
            }
        }
        
        // Добавляем курсор и лимит
        keyBuilder.append("cursor:").append(Objects.toString(cursor, "null")).append(":");
        keyBuilder.append("limit:").append(limit);
        
        return keyBuilder.toString();
    }
} 