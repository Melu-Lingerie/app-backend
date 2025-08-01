package ru.mellingerie.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mellingerie.products.entity.FilterValue;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilterValueRepository extends JpaRepository<FilterValue, Long> {
    
    List<FilterValue> findByFilterIdOrderBySortOrderAsc(Long filterId);
    
    Optional<FilterValue> findByFilterIdAndValue(Long filterId, String value);
    
    Optional<FilterValue> findByFilterIdAndDisplayName(Long filterId, String displayName);
    
    List<FilterValue> findByColorHex(String colorHex);
    
    @Query("SELECT fv FROM FilterValue fv WHERE fv.filter.id = :filterId AND LOWER(fv.value) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<FilterValue> searchByValue(@Param("filterId") Long filterId, @Param("searchTerm") String searchTerm);
    
    @Query("SELECT fv FROM FilterValue fv WHERE fv.filter.id = :filterId AND LOWER(fv.displayName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<FilterValue> searchByDisplayName(@Param("filterId") Long filterId, @Param("searchTerm") String searchTerm);
    
    @Query("SELECT fv FROM FilterValue fv WHERE fv.filter.id = :filterId AND (LOWER(fv.value) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(fv.displayName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<FilterValue> searchByValueOrDisplayName(@Param("filterId") Long filterId, @Param("searchTerm") String searchTerm);
    
    @Query("SELECT COUNT(fv) FROM FilterValue fv WHERE fv.filter.id = :filterId")
    long countByFilterId(@Param("filterId") Long filterId);
    
    boolean existsByFilterIdAndValue(Long filterId, String value);
    
    boolean existsByFilterIdAndDisplayName(Long filterId, String displayName);
} 