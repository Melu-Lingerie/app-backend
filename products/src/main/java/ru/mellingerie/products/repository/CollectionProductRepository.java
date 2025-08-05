package ru.mellingerie.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mellingerie.products.entity.CollectionProduct;

import java.util.List;

@Repository
public interface CollectionProductRepository extends JpaRepository<CollectionProduct, CollectionProduct.CollectionProductId> {
    
    List<CollectionProduct> findByCollectionId(Long collectionId);
    
    List<CollectionProduct> findByProductId(Long productId);

    @Query("SELECT cp FROM CollectionProduct cp WHERE cp.collection.id = :collectionId")
    List<CollectionProduct> findByCollectionIdQuery(@Param("collectionId") Long collectionId);
    
    @Query("SELECT cp FROM CollectionProduct cp WHERE cp.product.id = :productId")
    List<CollectionProduct> findByProductIdQuery(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(cp) FROM CollectionProduct cp WHERE cp.collection.id = :collectionId")
    long countByCollectionId(@Param("collectionId") Long collectionId);
    
    @Query("SELECT COUNT(cp) FROM CollectionProduct cp WHERE cp.product.id = :productId")
    long countByProductId(@Param("productId") Long productId);
    
    boolean existsByCollectionIdAndProductId(Long collectionId, Long productId);
} 