package ru.melulingerie.facade.wishlist.service;

public interface WishlistItemRemoveFacadeService {
    
    void removeItemFromWishlist(Long userId, Long wishlistItemId);
}