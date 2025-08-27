package ru.mellingerie.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mellingerie.products.domain.ProductReview;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {


}
