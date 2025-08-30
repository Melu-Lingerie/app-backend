package ru.melulingerie.products.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.melulingerie.products.domain.Product;
import ru.melulingerie.products.dto.request.ProductFilterRequestDto;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    default Page<Product> findByParams(ProductFilterRequestDto requestDto) {
        Specification<Product> specification = Specification
                .where(activeEq(requestDto.isActive()))
                .and(nameContains(requestDto.name()))
                .and(priceGte(requestDto.minPrice()))
                .and(priceLte(requestDto.maxPrice()))
                .and(colorIn(requestDto.colors()))
                .and(sizeIn(requestDto.sizes()))
                .and(onlyAvailableVariants(requestDto.onlyAvailableVariants()));

        return this.findAll(specification, requestDto.pageable());
    }

    // ---------- БАЗОВЫЕ ФИЛЬТРЫ ПО ПРОДУКТУ ----------

    static Specification<Product> nameContains(String q) {
        if (q == null || q.isBlank()) return null;
        String like = "%" + q.trim().toLowerCase() + "%";
        return (root, cq, cb) -> cb.like(cb.lower(root.get("name")), like);
    }

    static Specification<Product> activeEq(Boolean active) {
        if (active == null) return null;
        return (root, cq, cb) -> cb.equal(root.get("isActive"), active);
    }

    static Specification<Product> priceGte(BigDecimal min) {
        if (min == null) return null;
        return (root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("basePrice"), min);
    }

    static Specification<Product> priceLte(BigDecimal max) {
        if (max == null) return null;
        return (root, cq, cb) -> cb.lessThanOrEqualTo(root.get("basePrice"), max);
    }

    // ---------- ФИЛЬТРЫ ПО СВЯЗАННЫМ ВАРИАНТАМ (ProductVariant) ----------

    // Список цветов: точное совпадение по имени (регистронезависимо).
    static Specification<Product> colorIn(Collection<String> colors) {
        if (colors == null || colors.isEmpty()) return null;
        return (root, cq, cb) -> {
            var variants = root.join("variants"); // Join<Product, ProductVariant>
            List<String> normalized = colors.stream()
                    .filter(s -> s != null && !s.isBlank())
                    .map(s -> s.trim().toLowerCase())
                    .toList();
            if (normalized.isEmpty()) return cb.conjunction();

            // lower(variant.colorName) in (:colorsLower)
            return cb.lower(variants.get("colorName")).in(normalized);
        };
    }

    // Поиск по подстроке в названии цвета.
    static Specification<Product> colorLike(String colorLike) {
        if (colorLike == null || colorLike.isBlank()) return null;
        String like = "%" + colorLike.trim().toLowerCase() + "%";
        return (root, cq, cb) -> {
            var variants = root.join("variants");
            return cb.like(cb.lower(variants.get("colorName")), like);
        };
    }

    // Список размеров: точное совпадение (регистронезависимо).
    static Specification<Product> sizeIn(Collection<String> sizes) {
        if (sizes == null || sizes.isEmpty()) return null;
        return (root, cq, cb) -> {
            var variants = root.join("variants");
            List<String> normalized = sizes.stream()
                    .filter(s -> s != null && !s.isBlank())
                    .map(s -> s.trim().toLowerCase())
                    .toList();
            if (normalized.isEmpty()) return cb.conjunction();

            return cb.lower(variants.get("size")).in(normalized);
        };
    }

    // Только товары, у которых есть хотя бы один доступный вариант с положительным остатком.
    static Specification<Product> onlyAvailableVariants(Boolean onlyAvailable) {
        if (onlyAvailable == null || !onlyAvailable) return null;
        return (root, cq, cb) -> {
            var variants = root.join("variants");
            return cb.and(
                    cb.isTrue(variants.get("isAvailable")),
                    cb.greaterThan(variants.get("stockQuantity"), 0)
            );
        };
    }
}
