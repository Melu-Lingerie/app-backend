package ru.mellingerie.products.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemId implements Serializable {
    
    private Long wishlistId;
    private Long productId;
} 