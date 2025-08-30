package ru.melulingerie.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.melulingerie.products.domain.ProductReview;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {


}
