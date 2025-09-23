package ru.melulingerie.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.melulingerie.products.domain.Product;
import ru.melulingerie.products.projection.ProductIdCategoryIdProjection;

import java.util.Collection;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query(value = """
            select p.id as productId,
                   p.category_id as categoryId
            from products p
            """,
    nativeQuery = true)
    List<ProductIdCategoryIdProjection> findCategoryIdByProductIds(Collection<Long> productIds);
}
