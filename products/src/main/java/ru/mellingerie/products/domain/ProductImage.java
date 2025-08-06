package ru.mellingerie.products.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "product_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;
    
    @Column(name = "alt_text", length = 255)
    private String altText;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "image_type")
    private ImageType imageType = ImageType.GALLERY;
    
    public enum ImageType {
        MAIN, GALLERY, MODEL, DETAIL
    }
} 