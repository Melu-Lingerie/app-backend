package ru.melulingerie.facade.wishlist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.dto.WishlistResponseDto;
import ru.melulingerie.facade.wishlist.dto.WishlistApiListItemsResponseDto;
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

    public WishlistApiListItemsResponseDto getWishlist(Long userId) {
        // Междоменная валидация пользователя
        validateUserExists(userId);
        
        // Делегация в доменный сервис
        WishlistResponseDto domainResponse = wishlistQueryDomainService.getWishlist(userId);
        
        // Маппинг в API DTO
        return wishlistMapper.toFacadeWishListResponseDto(domainResponse);
    }

    private void validateUserExists(Long userId) {
        userService.getUserById(userId).orElseThrow(() -> {
            log.warn("User not found for userId: {}", userId);
            return new IllegalArgumentException("User not found with id: " + userId);
        });
    }
}
