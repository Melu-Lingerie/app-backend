package ru.mellingerie.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mellingerie.products.entity.Stylist;

import java.util.List;
import java.util.Optional;

@Repository
public interface StylistRepository extends JpaRepository<Stylist, Long> {
    
    List<Stylist> findByIsActiveTrueOrderByIdAsc();
    
    Optional<Stylist> findByInstagramHandle(String instagramHandle);
    
    @Query("SELECT s FROM Stylist s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND s.isActive = true")
    List<Stylist> searchByName(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT s FROM Stylist s WHERE LOWER(s.bio) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND s.isActive = true")
    List<Stylist> searchByBio(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT s FROM Stylist s WHERE (LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(s.bio) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND s.isActive = true")
    List<Stylist> searchByNameOrBio(@Param("searchTerm") String searchTerm);
    
    boolean existsByInstagramHandle(String instagramHandle);
    
    @Query("SELECT COUNT(s) FROM Stylist s WHERE s.isActive = true")
    long countActiveStylists();
} 