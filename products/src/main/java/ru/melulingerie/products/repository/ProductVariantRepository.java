package ru.melulingerie.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.melulingerie.products.domain.ProductVariant;
import ru.melulingerie.products.projection.ProductIdColorProjection;
import ru.melulingerie.products.projection.ProductIdPriceIdProjection;
import ru.melulingerie.products.projection.ProductIdSizeProjection;

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

    @Query("""
            select distinct pv.product.id as productId,
                   pv.size as size
            from ProductVariant pv
            where pv.product.id in :productIds
                  and pv.isAvailable = :isAvailable
            """)
    List<ProductIdSizeProjection> findSizesByProductIds(Set<Long> productIds, boolean b);

    @Query("""
            select distinct pv.product.id as productId,
                   pv.priceId as priceId
            from ProductVariant pv
            where pv.product.id in :productIds
                  and pv.isAvailable = :isAvailable
            """)
    List<ProductIdPriceIdProjection> findPricesByProductIds(Set<Long> productIds, boolean b);
}
