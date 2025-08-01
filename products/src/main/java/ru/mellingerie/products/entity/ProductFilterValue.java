package ru.mellingerie.products.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "product_filter_values")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterValue {
    
    @EmbeddedId
    private ProductFilterValueId id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("filterValueId")
    @JoinColumn(name = "filter_value_id")
    private FilterValue filterValue;
} 