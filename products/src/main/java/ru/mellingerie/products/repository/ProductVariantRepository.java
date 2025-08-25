package ru.mellingerie.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mellingerie.products.domain.ProductVariant;
import ru.mellingerie.products.projection.ProductIdColorProjection;

import java.util.List;
import java.util.Set;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    @Query("""
            select distinct pv.product.id as productId,
                   pv.colorName as colorName
            from ProductVariant pv
            where pv.product.id in :productIds
                  and pv.isAvailable = :isAvailable
            """)
    List<ProductIdColorProjection> findColorsByProductIds(@Param("productIds") Set<Long> productIds, @Param("isAvailable") Boolean isAvailable);
}
