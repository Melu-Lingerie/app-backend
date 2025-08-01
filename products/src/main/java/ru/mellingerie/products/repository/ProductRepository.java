package ru.mellingerie.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mellingerie.products.entity.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findBySlug(String slug);
    
    Optional<Product> findBySku(String sku);
    
    List<Product> findByCategoryIdOrderByCreatedAtDesc(Long categoryId);
    
    List<Product> findByIsActiveTrueOrderByCreatedAtDesc();
    
    List<Product> findByCategoryIdAndIsActiveTrueOrderByCreatedAtDesc(Long categoryId);
    
    @Query("SELECT p FROM Product p WHERE p.basePrice BETWEEN :minPrice AND :maxPrice AND p.isActive = true")
    List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND p.isActive = true")
    List<Product> searchByName(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT p FROM Product p WHERE LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND p.isActive = true")
    List<Product> searchByDescription(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT p FROM Product p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND p.isActive = true")
    List<Product> searchByNameOrDescription(@Param("searchTerm") String searchTerm);
    
    boolean existsBySlug(String slug);
    
    boolean existsBySku(String sku);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    long countByCategoryId(@Param("categoryId") Long categoryId);
} 