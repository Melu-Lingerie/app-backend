package ru.mellingerie.wishlist.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}


