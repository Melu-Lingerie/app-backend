package ru.melulingerie.service;

import ru.melulingerie.dto.GetWishlistResponseDto;

public interface WishlistQueryDomainService {
    
    GetWishlistResponseDto getWishlist(Long userId);
}