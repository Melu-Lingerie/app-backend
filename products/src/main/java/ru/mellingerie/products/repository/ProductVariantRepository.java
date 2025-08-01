package ru.mellingerie.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mellingerie.products.entity.ProductVariant;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    
    List<ProductVariant> findByProductId(Long productId);
    
    List<ProductVariant> findByProductIdAndIsAvailableTrue(Long productId);
    
    Optional<ProductVariant> findByProductIdAndColorNameAndSize(Long productId, String colorName, String size);
    
    List<ProductVariant> findByProductIdAndColorName(Long productId, String colorName);
    
    List<ProductVariant> findByProductIdAndSize(Long productId, String size);
    
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.stockQuantity > 0")
    List<ProductVariant> findAvailableVariantsByProductId(@Param("productId") Long productId);
    
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.stockQuantity > :minQuantity")
    List<ProductVariant> findVariantsWithStockByProductId(@Param("productId") Long productId, @Param("minQuantity") Integer minQuantity);
    
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.additionalPrice BETWEEN :minPrice AND :maxPrice")
    List<ProductVariant> findByAdditionalPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    @Query("SELECT COUNT(pv) FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.stockQuantity > 0")
    long countAvailableVariantsByProductId(@Param("productId") Long productId);
} 