package ru.melulingerie.products.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_id_seq")
    @SequenceGenerator(name = "product_id_seq", sequenceName = "product_id_seq", allocationSize = 10)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "article_number", nullable = false, length = 255)
    private String articleNumber;
    
    @Column(name = "slug", nullable = false, length = 255, unique = true)
    private String slug;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "set_id")
    private UUID setId;

    @Column(name = "sku", length = 50, unique = true)
    private String sku;
    
    @Column(name = "material", columnDefinition = "TEXT")
    private String material;
    
    @Column(name = "care_instructions", columnDefinition = "TEXT")
    private String careInstructions;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "main_media_id")
    private Long mainMediaId;
    
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "price_id", nullable = false)
    private Long priceId;

    @ToString.Exclude
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ProductVariant> variants = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductReview> reviews = new ArrayList<>();

    @Column(name = "score")
    private Float score;

    @ToString.Exclude
    @OneToMany(mappedBy = "product")
    private List<CollectionProduct> collections = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 