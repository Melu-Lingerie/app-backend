package ru.mellingerie.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.mellingerie.products.entity.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    Optional<Category> findBySlug(String slug);
    
    List<Category> findByParentIsNullOrderBySortOrderAsc();
    
    List<Category> findByParentIdOrderBySortOrderAsc(Long parentId);
    
    List<Category> findByIsActiveTrueOrderBySortOrderAsc();
    
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL AND c.isActive = true ORDER BY c.sortOrder ASC")
    List<Category> findActiveRootCategories();
    
    boolean existsBySlug(String slug);
} 