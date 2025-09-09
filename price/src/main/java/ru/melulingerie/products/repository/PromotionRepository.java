package ru.melulingerie.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.melulingerie.products.domain.Promotion;

import java.time.Instant;
import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    @Query("""
              SELECT pr
              FROM Promotion pr
              WHERE pr.scope = :scope
                AND pr.validFrom <= :now
                AND (pr.validTo IS NULL OR pr.validTo > :now)
              ORDER BY pr.priority ASC
            """)
    List<Promotion> findActiveByScope(@Param("scope") String scope,
                                      @Param("now") Instant now);



}
