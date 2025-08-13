package ru.melulingerie.service;

public interface WishlistItemRemoveDomainService {
    
    void removeWishlistItem(Long userId, Long itemId);
}