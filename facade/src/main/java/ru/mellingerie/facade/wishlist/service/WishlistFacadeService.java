package ru.mellingerie.facade.wishlist.service;

import ru.mellingerie.facade.wishlist.dto.*;

public interface WishlistFacadeService {
    WishlistResponseDto getWishlist(Long userId);
    AddToWishlistResponseDto add(Long userId, AddToWishlistRequestDto request);
    void remove(Long userId, Long itemId);
    void clear(Long userId);
}


