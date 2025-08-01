package ru.mellingerie.products.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "filters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Filter {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "filter_type", nullable = false)
    private FilterType filterType;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @OneToMany(mappedBy = "filter", cascade = CascadeType.ALL)
    private List<FilterValue> filterValues;
    
    public enum FilterType {
        COLOR, SIZE, CATEGORY, MATERIAL, STYLE
    }
} 