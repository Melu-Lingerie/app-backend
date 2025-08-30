package ru.melulingerie.products.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "product_reviews",
        indexes = {
                @Index(name = "ix_reviews_product", columnList = "product_id"),
                @Index(name = "ix_reviews_rating", columnList = "rating")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductReview {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "rating", nullable = false)
    private Integer rating;
    
    @Column(name = "review_text", columnDefinition = "TEXT")
    private String reviewText;

    //todo о чем поле?
    @Column(name = "reviewer_name", length = 100)
    private String reviewerName;
    
    @Column(name = "is_verified_purchase")
    private Boolean isVerifiedPurchase = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "is_approved")
    private Boolean isApproved = false;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 