package ru.melulingerie.facade.wishlist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.dto.GetWishlistResponseDto;
import ru.melulingerie.facade.wishlist.dto.GetWishlistListItemsResponseDto;
import ru.melulingerie.facade.wishlist.mapper.WishlistMapper;
import ru.melulingerie.facade.wishlist.mocks.UserService;
import ru.melulingerie.service.WishlistQueryDomainService;

/**
 * Фасадный сервис для получения wishlist
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistQueryFacadeService {

    private final WishlistQueryDomainService wishlistQueryDomainService;
    private final WishlistMapper wishlistMapper;
    private final UserService userService;

    public GetWishlistListItemsResponseDto getWishlist(Long userId) {
        validateUserExists(userId);
        
        GetWishlistResponseDto domainResponse = wishlistQueryDomainService.getWishlist(userId);
        
        return wishlistMapper.toFacadeWishListResponseDto(domainResponse);
    }

    private void validateUserExists(Long userId) {
        userService.getUserById(userId).orElseThrow(() -> {
            log.warn("User not found for userId: {}", userId);
            return new IllegalArgumentException("User not found with id: " + userId);
        });
    }
}
