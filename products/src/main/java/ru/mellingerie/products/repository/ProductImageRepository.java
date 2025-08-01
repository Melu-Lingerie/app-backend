package ru.mellingerie.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mellingerie.products.entity.ProductImage;
import ru.mellingerie.products.entity.ProductImage.ImageType;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    
    List<ProductImage> findByProductIdOrderBySortOrderAsc(Long productId);
    
    List<ProductImage> findByProductIdAndImageTypeOrderBySortOrderAsc(Long productId, ImageType imageType);
    
    Optional<ProductImage> findByProductIdAndImageType(Long productId, ImageType imageType);
    
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId AND pi.imageType = 'MAIN'")
    Optional<ProductImage> findMainImageByProductId(@Param("productId") Long productId);
    
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId AND pi.imageType = 'GALLERY' ORDER BY pi.sortOrder ASC")
    List<ProductImage> findGalleryImagesByProductId(@Param("productId") Long productId);
    
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId AND pi.imageType = 'MODEL' ORDER BY pi.sortOrder ASC")
    List<ProductImage> findModelImagesByProductId(@Param("productId") Long productId);
    
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId AND pi.imageType = 'DETAIL' ORDER BY pi.sortOrder ASC")
    List<ProductImage> findDetailImagesByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(pi) FROM ProductImage pi WHERE pi.product.id = :productId")
    long countByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(pi) FROM ProductImage pi WHERE pi.product.id = :productId AND pi.imageType = :imageType")
    long countByProductIdAndImageType(@Param("productId") Long productId, @Param("imageType") ImageType imageType);
} 