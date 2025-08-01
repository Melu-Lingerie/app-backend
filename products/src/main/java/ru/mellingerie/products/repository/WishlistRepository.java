package ru.mellingerie.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mellingerie.products.entity.Wishlist;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    
    Optional<Wishlist> findByUserId(Long userId);
    
    List<Wishlist> findByCreatedAtAfter(LocalDateTime since);
    
    @Query("SELECT w FROM Wishlist w WHERE w.userId = :userId")
    Optional<Wishlist> findByUserIdQuery(@Param("userId") Long userId);
    
    @Query("SELECT w FROM Wishlist w WHERE w.createdAt >= :since ORDER BY w.createdAt DESC")
    List<Wishlist> findRecentWishlists(@Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(w) FROM Wishlist w WHERE w.userId = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    boolean existsByUserId(Long userId);
} 