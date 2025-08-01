package ru.mellingerie.products.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "filter_values")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilterValue {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filter_id")
    private Filter filter;
    
    @Column(name = "value", nullable = false, length = 100)
    private String value;
    
    @Column(name = "display_name", length = 100)
    private String displayName;
    
    @Column(name = "color_hex", length = 7)
    private String colorHex;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
} 