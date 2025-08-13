package ru.melulingerie.service;

import ru.melulingerie.dto.AddItemToWishlistRequestDto;
import ru.melulingerie.dto.AddItemToWishlistResponseDto;

public interface WishlistItemAddDomainService {
    
    AddItemToWishlistResponseDto addWishlistItemToWishlist(Long userId, AddItemToWishlistRequestDto request);
}