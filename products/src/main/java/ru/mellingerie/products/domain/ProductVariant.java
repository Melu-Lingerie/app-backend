package ru.mellingerie.products.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "color_name", length = 50)
    private String colorName;
    
    @Column(name = "color_hex", length = 7)
    private String colorHex;
    
    @Column(name = "size", length = 10)
    private String size;
    
    @Column(name = "stock_quantity")
    private Integer stockQuantity = 0;
    
    @Column(name = "additional_price", precision = 10, scale = 2)
    private BigDecimal additionalPrice = BigDecimal.ZERO;
    
    @Column(name = "is_available")
    private Boolean isAvailable = true;
} 