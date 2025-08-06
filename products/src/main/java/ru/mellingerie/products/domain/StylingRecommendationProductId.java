package ru.mellingerie.products.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StylingRecommendationProductId implements Serializable {
    
    private Long recommendationId;
    private Long productId;
} 