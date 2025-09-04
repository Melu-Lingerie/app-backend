package ru.melulingerie.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "wishlists", indexes = {
        @Index(name = "idx_wishlists_user", columnList = "user_id", unique = true)
})
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wishlists_id_seq")
    @SequenceGenerator(name = "wishlists_id_seq", sequenceName = "wishlists_id_seq", allocationSize = 10)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @OneToMany(mappedBy = "wishlist")
    private List<WishlistItem> wishlistItems = new ArrayList<>();
}


