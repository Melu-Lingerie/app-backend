package ru.mellingerie.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mellingerie.products.entity.Filter;
import ru.mellingerie.products.entity.Filter.FilterType;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilterRepository extends JpaRepository<Filter, Long> {
    
    List<Filter> findAllByOrderBySortOrderAsc();
    
    List<Filter> findByFilterTypeOrderBySortOrderAsc(FilterType filterType);
    
    Optional<Filter> findByName(String name);
    
    Optional<Filter> findByNameAndFilterType(String name, FilterType filterType);
    
    @Query("SELECT f FROM Filter f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Filter> searchByName(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT f FROM Filter f WHERE f.filterType = :filterType ORDER BY f.sortOrder ASC")
    List<Filter> findByFilterType(@Param("filterType") FilterType filterType);
    
    boolean existsByName(String name);
    
    boolean existsByNameAndFilterType(String name, FilterType filterType);
    
    @Query("SELECT COUNT(f) FROM Filter f WHERE f.filterType = :filterType")
    long countByFilterType(@Param("filterType") FilterType filterType);
} 