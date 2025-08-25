package ru.mellingerie.products.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_variant_media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantMedia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;
    
    @Column(name = "media_id")
    private Long mediaId;

    @Column(name = "order")
    private Integer order;
} 