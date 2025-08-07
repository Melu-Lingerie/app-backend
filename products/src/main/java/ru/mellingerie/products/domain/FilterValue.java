package ru.mellingerie.products.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "filter_values")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilterValue {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filter_id", nullable = false)
    private Filter filter;
    
    @Column(name = "value", nullable = false, length = 100)
    private String value;
    
    @Column(name = "display_name", length = 100)
    private String displayName;
    
    @Column(name = "color_hex", length = 7)
    private String colorHex;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @ManyToMany(mappedBy = "filterValues")
    private List<Product> products;
} 