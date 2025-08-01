package ru.mellingerie.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mellingerie.products.entity.StylingRecommendationProduct;
import ru.mellingerie.products.entity.StylingRecommendationProduct.ProductRole;

import java.util.List;

@Repository
public interface StylingRecommendationProductRepository extends JpaRepository<StylingRecommendationProduct, StylingRecommendationProduct.StylingRecommendationProductId> {
    
    List<StylingRecommendationProduct> findByStylingRecommendationId(Long recommendationId);
    
    List<StylingRecommendationProduct> findByProductId(Long productId);
    
    List<StylingRecommendationProduct> findByStylingRecommendationIdAndRole(Long recommendationId, ProductRole role);
    
    List<StylingRecommendationProduct> findByProductIdAndRole(Long productId, ProductRole role);
    
    @Query("SELECT srp FROM StylingRecommendationProduct srp WHERE srp.stylingRecommendation.id = :recommendationId AND srp.role = 'MAIN'")
    List<StylingRecommendationProduct> findMainProductsByRecommendationId(@Param("recommendationId") Long recommendationId);
    
    @Query("SELECT srp FROM StylingRecommendationProduct srp WHERE srp.stylingRecommendation.id = :recommendationId AND srp.role = 'COMPLEMENT'")
    List<StylingRecommendationProduct> findComplementProductsByRecommendationId(@Param("recommendationId") Long recommendationId);
    
    @Query("SELECT srp FROM StylingRecommendationProduct srp WHERE srp.stylingRecommendation.id = :recommendationId AND srp.role = 'ACCESSORY'")
    List<StylingRecommendationProduct> findAccessoryProductsByRecommendationId(@Param("recommendationId") Long recommendationId);
    
    @Query("SELECT COUNT(srp) FROM StylingRecommendationProduct srp WHERE srp.stylingRecommendation.id = :recommendationId")
    long countByRecommendationId(@Param("recommendationId") Long recommendationId);
    
    @Query("SELECT COUNT(srp) FROM StylingRecommendationProduct srp WHERE srp.product.id = :productId")
    long countByProductId(@Param("productId") Long productId);
    
    boolean existsByStylingRecommendationIdAndProductId(Long recommendationId, Long productId);
} 