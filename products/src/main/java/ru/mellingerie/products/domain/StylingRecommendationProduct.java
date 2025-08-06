package ru.mellingerie.products.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "styling_recommendation_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StylingRecommendationProduct {
    
    @EmbeddedId
    private StylingRecommendationProductId id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("recommendationId")
    @JoinColumn(name = "recommendation_id")
    private StylingRecommendation stylingRecommendation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private ProductRole role = ProductRole.MAIN;
    
    public enum ProductRole {
        MAIN, COMPLEMENT, ACCESSORY
    }
} 