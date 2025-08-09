package ru.mellingerie.wishlist.model;

import java.time.LocalDateTime;

public record WishlistItemModel(
        Long id,
        Long productId,
        Long variantId,
        LocalDateTime addedAt
) {}


