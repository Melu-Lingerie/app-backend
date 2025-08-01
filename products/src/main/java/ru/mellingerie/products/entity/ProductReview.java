package ru.mellingerie.products.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductReview {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "rating")
    private Integer rating;
    
    @Column(name = "review_text", columnDefinition = "TEXT")
    private String reviewText;
    
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