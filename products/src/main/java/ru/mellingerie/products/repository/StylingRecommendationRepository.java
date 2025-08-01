package ru.mellingerie.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mellingerie.products.entity.StylingRecommendation;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StylingRecommendationRepository extends JpaRepository<StylingRecommendation, Long> {
    
    List<StylingRecommendation> findByStylistIdOrderByCreatedAtDesc(Long stylistId);
    
    List<StylingRecommendation> findByStylistIdAndStylistIsActiveTrueOrderByCreatedAtDesc(Long stylistId);
    
    @Query("SELECT sr FROM StylingRecommendation sr WHERE sr.createdAt >= :since ORDER BY sr.createdAt DESC")
    List<StylingRecommendation> findRecentRecommendations(@Param("since") LocalDateTime since);
    
    @Query("SELECT sr FROM StylingRecommendation sr WHERE LOWER(sr.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<StylingRecommendation> searchByTitle(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT sr FROM StylingRecommendation sr WHERE LOWER(sr.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<StylingRecommendation> searchByDescription(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT sr FROM StylingRecommendation sr WHERE (LOWER(sr.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(sr.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<StylingRecommendation> searchByTitleOrDescription(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT COUNT(sr) FROM StylingRecommendation sr WHERE sr.stylist.id = :stylistId")
    long countByStylistId(@Param("stylistId") Long stylistId);
    
    @Query("SELECT sr FROM StylingRecommendation sr WHERE sr.stylist.isActive = true ORDER BY sr.createdAt DESC")
    List<StylingRecommendation> findRecommendationsByActiveStylists();
} 