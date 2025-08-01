package ru.mellingerie.products.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "collections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Collection {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "slug", nullable = false, length = 100, unique = true)
    private String slug;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "image_url", length = 255)
    private String imageUrl;
    
    @Column(name = "is_featured")
    private Boolean isFeatured = false;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL)
    private List<CollectionProduct> collectionProducts;
} 