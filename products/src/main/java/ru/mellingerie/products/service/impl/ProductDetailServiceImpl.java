package ru.mellingerie.products.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ru.mellingerie.products.dto.ProductDetailDTO;
import ru.mellingerie.products.exception.ProductNotFoundException;
import ru.mellingerie.products.repository.ProductDetailRepository;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductDetailServiceImpl implements ru.mellingerie.products.service.ProductDetailService {
    
    private final ProductDetailRepository detailRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public ProductDetailDTO getProductDetail(Long productId, Boolean includeReviews, Boolean includeRecommendations) {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("Product ID must be positive");
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Построение кэш-ключа
            String cacheKey = buildDetailCacheKey(productId, includeReviews, includeRecommendations);
            
            // Попытка получить из кэша
            ProductDetailDTO cached = (ProductDetailDTO) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.debug("Cache hit for product detail: {}", productId);
                return cached;
            }
            
            // Выборка из БД
            var productDetail = detailRepository.findDetailById(productId, includeReviews, includeRecommendations);
            
            if (productDetail.isEmpty()) {
                log.warn("Product not found: {}", productId);
                throw new ProductNotFoundException(productId);
            }
            
            ProductDetailDTO result = productDetail.get();
            
            // Дополнительная обработка медиа URLs
            var optimizedMedia = optimizeMediaUrls(result.media());
            result = new ProductDetailDTO(
                result.id(),
                result.name(),
                result.slug(),
                result.description(),
                result.basePrice(),
                result.currentPrice(),
                result.material(),
                result.careInstructions(),
                result.createdAt(),
                result.categoryId(),
                result.categoryName(),
                result.categorySlug(),
                optimizedMedia,
                result.variants(),
                result.reviewsSummary(),
                result.stylingRecommendations(),
                result.breadcrumbs()
            );
            
            // Сохранение в кэш
            redisTemplate.opsForValue().set(cacheKey, result, Duration.ofSeconds(300));
            log.debug("Cached product detail for: {}", productId);
            
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("Product detail request completed in {}ms for product: {}", executionTime, productId);
            
            return result;
            
        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during product detail request for product: {}", productId, e);
            throw e;
        }
    }
    
    @Override
    public ProductDetailDTO getProductDetailBySlug(String slug, Boolean includeReviews, Boolean includeRecommendations) {
        if (slug == null || slug.trim().isEmpty()) {
            throw new IllegalArgumentException("Product slug cannot be empty");
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Построение кэш-ключа
            String cacheKey = buildDetailBySlugCacheKey(slug, includeReviews, includeRecommendations);
            
            // Попытка получить из кэша
            ProductDetailDTO cached = (ProductDetailDTO) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.debug("Cache hit for product detail by slug: {}", slug);
                return cached;
            }
            
            // Выборка из БД
            var productDetail = detailRepository.findDetailBySlug(slug, includeReviews, includeRecommendations);
            
            if (productDetail.isEmpty()) {
                log.warn("Product not found by slug: {}", slug);
                throw new ProductNotFoundException(slug, "slug");
            }
            
            ProductDetailDTO result = productDetail.get();
            
            // Дополнительная обработка медиа URLs
            var optimizedMedia = optimizeMediaUrls(result.media());
            result = new ProductDetailDTO(
                result.id(),
                result.name(),
                result.slug(),
                result.description(),
                result.basePrice(),
                result.currentPrice(),
                result.material(),
                result.careInstructions(),
                result.createdAt(),
                result.categoryId(),
                result.categoryName(),
                result.categorySlug(),
                optimizedMedia,
                result.variants(),
                result.reviewsSummary(),
                result.stylingRecommendations(),
                result.breadcrumbs()
            );
            
            // Сохранение в кэш
            redisTemplate.opsForValue().set(cacheKey, result, Duration.ofSeconds(300));
            log.debug("Cached product detail by slug for: {}", slug);
            
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("Product detail by slug request completed in {}ms for slug: {}", executionTime, slug);
            
            return result;
            
        } catch (ProductNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during product detail by slug request for slug: {}", slug, e);
            throw e;
        }
    }
    
    private String buildDetailCacheKey(Long productId, Boolean includeReviews, Boolean includeRecommendations) {
        return String.format("product_detail:%d:reviews:%s:recs:%s", 
            productId, 
            includeReviews != null ? includeReviews : "true",
            includeRecommendations != null ? includeRecommendations : "true"
        );
    }
    
    private String buildDetailBySlugCacheKey(String slug, Boolean includeReviews, Boolean includeRecommendations) {
        return String.format("product_detail_slug:%s:reviews:%s:recs:%s", 
            slug, 
            includeReviews != null ? includeReviews : "true",
            includeRecommendations != null ? includeRecommendations : "true"
        );
    }
    
    private java.util.List<ru.mellingerie.products.dto.MediaDTO> optimizeMediaUrls(
            java.util.List<ru.mellingerie.products.dto.MediaDTO> media) {
        
        if (media == null) {
            return null;
        }
        
        return media.stream().map(m -> {
            if ("image".equals(m.type())) {
                // Добавляем WebP версии и responsive sizes
                return new MediaDTO(
                    m.type(),
                    m.url(),
                    generateWebPUrl(m.url()),
                    generateResponsiveSrcSet(m.url()),
                    m.alt(),
                    m.sortOrder(),
                    m.thumbnail(),
                    m.hlsUrl()
                );
            } else if ("video".equals(m.type())) {
                // Добавляем thumbnail и multiple bitrates
                return new MediaDTO(
                    m.type(),
                    m.url(),
                    m.webpUrl(),
                    m.srcSet(),
                    m.alt(),
                    m.sortOrder(),
                    generateVideoThumbnail(m.url()),
                    generateHlsUrl(m.url())
                );
            }
            return m;
        }).toList();
    }
    
    // Заглушки для оптимизации медиа URLs
    private String generateWebPUrl(String originalUrl) {
        // В реальной реализации здесь была бы логика генерации WebP URL
        return originalUrl.replaceFirst("\\.(jpg|jpeg|png)$", ".webp");
    }
    
    private String generateResponsiveSrcSet(String originalUrl) {
        // В реальной реализации здесь была бы логика генерации responsive srcset
        return originalUrl + " 1x, " + originalUrl.replaceFirst("\\.(jpg|jpeg|png)$", "@2x.$1") + " 2x";
    }
    
    private String generateVideoThumbnail(String videoUrl) {
        // В реальной реализации здесь была бы логика генерации thumbnail
        return videoUrl.replaceFirst("\\.(mp4|webm)$", "_thumb.jpg");
    }
    
    private String generateHlsUrl(String videoUrl) {
        // В реальной реализации здесь была бы логика генерации HLS URL
        return videoUrl.replaceFirst("\\.(mp4|webm)$", ".m3u8");
    }
} 