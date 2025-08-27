package ru.mellingerie.products.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_variants",
        indexes = {
                @Index(name = "ix_product_variants_product", columnList = "product_id"),
                @Index(name = "ix_product_variants_color", columnList = "color_name"),
                @Index(name = "ix_product_variants_size", columnList = "size")
        })
@Getter
@Setter
@NoArgsConstructor
public class ProductVariant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "color_name", length = 50)
    private String colorName;
    
    @Column(name = "size", length = 10)
    private String size;
    
    @Column(name = "stock_quantity")
    private Integer stockQuantity = 0;

    //todo возможно заменить на порудукт прайс
    @Column(name = "additional_price", precision = 10, scale = 2)
    private BigDecimal additionalPrice = BigDecimal.ZERO;
    
    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ProductVariantMedia> productVariantMedia = new ArrayList<>();
} 