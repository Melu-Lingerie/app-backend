package ru.melulingerie.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "wishlist_items", indexes = {
        @Index(name = "idx_wishlist_items_wishlist", columnList = "wishlist_id"),
        @Index(name = "idx_wishlist_items_unique", columnList = "wishlist_id, product_id", unique = true)
})
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wishlist_items_id_seq")
    @SequenceGenerator(name = "wishlist_items_id_seq", sequenceName = "wishlist_items_id_seq", allocationSize = 10)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wishlist_id", nullable = false)
    private Wishlist wishlist;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}