package ru.mellingerie.products.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "wishlist_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItem {
    
    @EmbeddedId
    private WishlistItemId id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("wishlistId")
    @JoinColumn(name = "wishlist_id")
    private Wishlist wishlist;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;
    
    @Column(name = "added_at")
    private LocalDateTime addedAt;
    
    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
    }
} 