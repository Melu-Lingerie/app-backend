package ru.mellingerie.facade.wishlist.dto;

import java.time.LocalDateTime;

public record WishlistItemDto(Long id, Long productId, Long variantId, LocalDateTime addedAt) {}


