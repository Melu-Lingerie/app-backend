package ru.melulingerie.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.melulingerie.products.domain.Price;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PriceRepository extends JpaRepository<Price, Long> {

    @Query("""
              SELECT p
              FROM Price p
              WHERE p.productVariantId = :productId
                AND p.validFrom <= :now
                AND (p.validTo IS NULL OR p.validTo > :now)
              ORDER BY p.validFrom DESC
            """)
    Optional<Price> findCurrentByProductId(@Param("productId") Long productId,
                                           @Param("now") Instant now);

    @Query(value = """
              SELECT DISTINCT ON (p.product_id) p.*
              FROM prices p
              WHERE p.product_variant_id = ANY(:ids)
                AND p.valid_from <= :now
                AND (p.valid_to IS NULL OR p.valid_to > :now)
              ORDER BY p.product_id, p.valid_from DESC
            """, nativeQuery = true)
    List<Price> findCurrentByProductIds(@Param("ids") long[] productIds, @Param("now") Instant now);
}
