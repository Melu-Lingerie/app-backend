package ru.mellingerie.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mellingerie.products.entity.Collection;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {
    
    Optional<Collection> findBySlug(String slug);
    
    List<Collection> findByIsFeaturedTrueOrderBySortOrderAsc();
    
    List<Collection> findAllByOrderBySortOrderAsc();
    
    @Query("SELECT c FROM Collection c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Collection> searchByName(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT c FROM Collection c WHERE LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Collection> searchByDescription(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT c FROM Collection c WHERE (LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Collection> searchByNameOrDescription(@Param("searchTerm") String searchTerm);
    
    boolean existsBySlug(String slug);
    
    @Query("SELECT COUNT(c) FROM Collection c WHERE c.isFeatured = true")
    long countFeaturedCollections();
} 