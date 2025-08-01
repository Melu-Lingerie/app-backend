package ru.mellingerie.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mellingerie.products.entity.ProductFilterValue;

import java.util.List;

@Repository
public interface ProductFilterValueRepository extends JpaRepository<ProductFilterValue, ProductFilterValue.ProductFilterValueId> {
    
    List<ProductFilterValue> findByProductId(Long productId);
    
    List<ProductFilterValue> findByFilterValueId(Long filterValueId);
    
    @Query("SELECT pfv FROM ProductFilterValue pfv WHERE pfv.product.id = :productId")
    List<ProductFilterValue> findByProductIdQuery(@Param("productId") Long productId);
    
    @Query("SELECT pfv FROM ProductFilterValue pfv WHERE pfv.filterValue.id = :filterValueId")
    List<ProductFilterValue> findByFilterValueIdQuery(@Param("filterValueId") Long filterValueId);
    
    @Query("SELECT pfv FROM ProductFilterValue pfv WHERE pfv.filterValue.filter.id = :filterId")
    List<ProductFilterValue> findByFilterId(@Param("filterId") Long filterId);
    
    @Query("SELECT pfv FROM ProductFilterValue pfv WHERE pfv.product.id = :productId AND pfv.filterValue.filter.id = :filterId")
    List<ProductFilterValue> findByProductIdAndFilterId(@Param("productId") Long productId, @Param("filterId") Long filterId);
    
    @Query("SELECT COUNT(pfv) FROM ProductFilterValue pfv WHERE pfv.product.id = :productId")
    long countByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(pfv) FROM ProductFilterValue pfv WHERE pfv.filterValue.id = :filterValueId")
    long countByFilterValueId(@Param("filterValueId") Long filterValueId);
    
    boolean existsByProductIdAndFilterValueId(Long productId, Long filterValueId);
} 