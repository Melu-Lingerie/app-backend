package ru.melulingerie.facade.wishlist.service;

import ru.melulingerie.facade.wishlist.dto.AddWishlistRequestDto;
import ru.melulingerie.facade.wishlist.dto.AddWishlistResponseDto;

public interface WishlistItemAddFacadeService {
    
    AddWishlistResponseDto addItemToWishlist(Long userId, AddWishlistRequestDto request);
}