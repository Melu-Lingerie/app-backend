package ru.melulingerie.facade.wishlist.service;

import ru.melulingerie.facade.wishlist.dto.GetWishlistListItemsResponseDto;

public interface WishlistQueryFacadeService {
    
    GetWishlistListItemsResponseDto getWishlist(Long userId);
}