package ru.mellingerie.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mellingerie.products.entity.ProductReview;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    
    List<ProductReview> findByProductIdOrderByCreatedAtDesc(Long productId);
    
    List<ProductReview> findByProductIdAndIsApprovedTrueOrderByCreatedAtDesc(Long productId);
    
    List<ProductReview> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<ProductReview> findByProductIdAndUserId(Long productId, Long userId);
    
    List<ProductReview> findByRating(Integer rating);
    
    List<ProductReview> findByProductIdAndRating(Long productId, Integer rating);
    
    List<ProductReview> findByIsVerifiedPurchaseTrue();
    
    List<ProductReview> findByProductIdAndIsVerifiedPurchaseTrue(Long productId);
    
    @Query("SELECT pr FROM ProductReview pr WHERE pr.product.id = :productId AND pr.rating >= :minRating AND pr.isApproved = true ORDER BY pr.createdAt DESC")
    List<ProductReview> findApprovedReviewsByProductIdAndMinRating(@Param("productId") Long productId, @Param("minRating") Integer minRating);
    
    @Query("SELECT pr FROM ProductReview pr WHERE pr.createdAt >= :since ORDER BY pr.createdAt DESC")
    List<ProductReview> findRecentReviews(@Param("since") LocalDateTime since);
    
    @Query("SELECT AVG(pr.rating) FROM ProductReview pr WHERE pr.product.id = :productId AND pr.isApproved = true")
    Double getAverageRatingByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(pr) FROM ProductReview pr WHERE pr.product.id = :productId AND pr.isApproved = true")
    long countApprovedReviewsByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(pr) FROM ProductReview pr WHERE pr.product.id = :productId AND pr.rating = :rating AND pr.isApproved = true")
    long countApprovedReviewsByProductIdAndRating(@Param("productId") Long productId, @Param("rating") Integer rating);
    
    boolean existsByProductIdAndUserId(Long productId, Long userId);
} 