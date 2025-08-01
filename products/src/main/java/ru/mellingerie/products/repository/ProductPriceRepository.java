package ru.mellingerie.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mellingerie.products.entity.ProductPrice;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {
    
    List<ProductPrice> findByProductIdOrderByUpdatedAtDesc(Long productId);
    
    Optional<ProductPrice> findFirstByProductIdOrderByUpdatedAtDesc(Long productId);
    
    @Query("SELECT pp FROM ProductPrice pp WHERE pp.product.id = :productId ORDER BY pp.updatedAt DESC")
    List<ProductPrice> findLatestPricesByProductId(@Param("productId") Long productId);
    
    @Query("SELECT pp FROM ProductPrice pp WHERE pp.price BETWEEN :minPrice AND :maxPrice")
    List<ProductPrice> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    @Query("SELECT pp FROM ProductPrice pp WHERE pp.updatedAt >= :since ORDER BY pp.updatedAt DESC")
    List<ProductPrice> findRecentPriceUpdates(@Param("since") LocalDateTime since);
    
    @Query("SELECT pp FROM ProductPrice pp WHERE pp.product.id = :productId AND pp.updatedAt >= :since ORDER BY pp.updatedAt DESC")
    List<ProductPrice> findRecentPriceUpdatesByProductId(@Param("productId") Long productId, @Param("since") LocalDateTime since);
    
    @Query("SELECT MIN(pp.price) FROM ProductPrice pp WHERE pp.product.id = :productId")
    BigDecimal getMinPriceByProductId(@Param("productId") Long productId);
    
    @Query("SELECT MAX(pp.price) FROM ProductPrice pp WHERE pp.product.id = :productId")
    BigDecimal getMaxPriceByProductId(@Param("productId") Long productId);
    
    @Query("SELECT AVG(pp.price) FROM ProductPrice pp WHERE pp.product.id = :productId")
    BigDecimal getAveragePriceByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(pp) FROM ProductPrice pp WHERE pp.product.id = :productId")
    long countByProductId(@Param("productId") Long productId);
} 