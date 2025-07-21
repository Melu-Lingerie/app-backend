package ru.melulingerie.domain.media;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Represents detailed information about an image media type.
 * This entity stores image-specific attributes like dimensions and title,
 * and is linked to a generic ProductMedia entry.
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "images")
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id", nullable = false)
    private ProductMediaEntity media;

    @Column(nullable = false)
    private int width;

    @Column(nullable = false)
    private int height;

    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "image_category")
    private ImageCategory imageCategory = ImageCategory.PRODUCT;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}