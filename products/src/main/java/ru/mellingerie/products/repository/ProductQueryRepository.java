package ru.mellingerie.products.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.mellingerie.products.entity.*;

import java.math.BigDecimal;
import java.util.List;

import static ru.mellingerie.products.entity.QProduct.product;
import static ru.mellingerie.products.entity.QProductVariant.productVariant;
import static ru.mellingerie.products.entity.QCategory.category;
import static ru.mellingerie.products.entity.QProductReview.productReview;
import static ru.mellingerie.products.entity.QProductPrice.productPrice;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepository {
    
    private final JPAQueryFactory queryFactory;
    
    public List<Product> findProductsWithFilters(
            List<Long> categoryIds,
            List<String> colors,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            List<String> sizes,
            Boolean inStock,
            String searchTerm
    ) {
        return queryFactory
                .selectFrom(product)
                .leftJoin(product.category, category).fetchJoin()
                .leftJoin(product.variants, productVariant)
                .where(buildProductFilterPredicate(categoryIds, colors, minPrice, maxPrice, sizes, inStock, searchTerm))
                .distinct()
                .fetch();
    }
    
    public Page<Product> findProductsWithPagination(
            List<Long> categoryIds,
            List<String> colors,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            List<String> sizes,
            Boolean inStock,
            String searchTerm,
            Pageable pageable
    ) {
        long total = queryFactory
                .selectFrom(product)
                .leftJoin(product.category, category)
                .leftJoin(product.variants, productVariant)
                .where(buildProductFilterPredicate(categoryIds, colors, minPrice, maxPrice, sizes, inStock, searchTerm))
                .distinct()
                .fetchCount();
        
        List<Product> products = queryFactory
                .selectFrom(product)
                .leftJoin(product.category, category).fetchJoin()
                .leftJoin(product.variants, productVariant)
                .where(buildProductFilterPredicate(categoryIds, colors, minPrice, maxPrice, sizes, inStock, searchTerm))
                .distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        
        return new PageImpl<>(products, pageable, total);
    }
    
    public List<Product> findProductsInStock() {
        return queryFactory
                .selectFrom(product)
                .leftJoin(product.variants, productVariant)
                .where(
                        product.isActive.isTrue(),
                        productVariant.stockQuantity.gt(0)
                )
                .distinct()
                .fetch();
    }
    
    public List<Product> findProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return queryFactory
                .selectFrom(product)
                .where(
                        product.isActive.isTrue(),
                        product.basePrice.between(minPrice, maxPrice)
                )
                .fetch();
    }
    
    public List<Product> searchProducts(String searchTerm) {
        return queryFactory
                .selectFrom(product)
                .where(
                        product.isActive.isTrue(),
                        product.name.containsIgnoreCase(searchTerm)
                                .or(product.description.containsIgnoreCase(searchTerm))
                                .or(product.slug.containsIgnoreCase(searchTerm))
                )
                .fetch();
    }
    
    public List<Product> findTopRatedProducts(int limit) {
        return queryFactory
                .selectFrom(product)
                .leftJoin(product.reviews, productReview)
                .where(
                        product.isActive.isTrue(),
                        productReview.isApproved.isTrue()
                )
                .groupBy(product.id)
                .having(productReview.rating.avg().goe(4.0))
                .orderBy(productReview.rating.avg().desc())
                .limit(limit)
                .fetch();
    }
    
    public List<Product> findRecentlyAddedProducts(int limit) {
        return queryFactory
                .selectFrom(product)
                .where(product.isActive.isTrue())
                .orderBy(product.createdAt.desc())
                .limit(limit)
                .fetch();
    }
    
    public List<Product> findProductsByCategoryWithSubcategories(Long categoryId) {
        return queryFactory
                .selectFrom(product)
                .leftJoin(product.category, category)
                .where(
                        product.isActive.isTrue(),
                        category.id.eq(categoryId)
                                .or(category.parent.id.eq(categoryId))
                )
                .fetch();
    }
    
    private Predicate buildProductFilterPredicate(
            List<Long> categoryIds,
            List<String> colors,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            List<String> sizes,
            Boolean inStock,
            String searchTerm
    ) {
        BooleanBuilder predicate = new BooleanBuilder();
        
        predicate.and(product.isActive.isTrue());
        
        if (categoryIds != null && !categoryIds.isEmpty()) {
            predicate.and(product.category.id.in(categoryIds));
        }
        
        if (colors != null && !colors.isEmpty()) {
            predicate.and(productVariant.colorName.in(colors));
        }
        
        if (minPrice != null) {
            predicate.and(product.basePrice.goe(minPrice));
        }
        
        if (maxPrice != null) {
            predicate.and(product.basePrice.loe(maxPrice));
        }
        
        if (sizes != null && !sizes.isEmpty()) {
            predicate.and(productVariant.size.in(sizes));
        }
        
        if (inStock != null && inStock) {
            predicate.and(productVariant.stockQuantity.gt(0));
        }
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            predicate.and(
                    product.name.containsIgnoreCase(searchTerm)
                            .or(product.description.containsIgnoreCase(searchTerm))
                            .or(product.slug.containsIgnoreCase(searchTerm))
            );
        }
        
        return predicate;
    }
} 