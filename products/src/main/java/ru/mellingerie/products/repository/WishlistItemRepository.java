package ru.mellingerie.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mellingerie.products.entity.WishlistItem;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, WishlistItem.WishlistItemId> {
    
    List<WishlistItem> findByWishlistId(Long wishlistId);
    
    List<WishlistItem> findByProductId(Long productId);
    
    @Query("SELECT wi FROM WishlistItem wi WHERE wi.wishlist.id = :wishlistId")
    List<WishlistItem> findByWishlistIdQuery(@Param("wishlistId") Long wishlistId);
    
    @Query("SELECT wi FROM WishlistItem wi WHERE wi.product.id = :productId")
    List<WishlistItem> findByProductIdQuery(@Param("productId") Long productId);
    
    @Query("SELECT wi FROM WishlistItem wi WHERE wi.addedAt >= :since ORDER BY wi.addedAt DESC")
    List<WishlistItem> findRecentItems(@Param("since") LocalDateTime since);
    
    @Query("SELECT wi FROM WishlistItem wi WHERE wi.wishlist.id = :wishlistId AND wi.addedAt >= :since ORDER BY wi.addedAt DESC")
    List<WishlistItem> findRecentItemsByWishlistId(@Param("wishlistId") Long wishlistId, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(wi) FROM WishlistItem wi WHERE wi.wishlist.id = :wishlistId")
    long countByWishlistId(@Param("wishlistId") Long wishlistId);
    
    @Query("SELECT COUNT(wi) FROM WishlistItem wi WHERE wi.product.id = :productId")
    long countByProductId(@Param("productId") Long productId);
    
    boolean existsByWishlistIdAndProductId(Long wishlistId, Long productId);
} 