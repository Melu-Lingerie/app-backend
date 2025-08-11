package ru.melulingerie.facade.wishlist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.facade.wishlist.mocks.UserService;
import ru.melulingerie.service.WishlistItemRemoveDomainService;

/**
 * Фасадный сервис для удаления элементов из wishlist
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistItemRemoveFacadeService {

    private final WishlistItemRemoveDomainService wishlistItemRemoveDomainService;
    private final UserService userService;

    public void removeItemFromWishlist(Long userId, Long itemId) {
        // Междоменная валидация пользователя
        validateUserExists(userId);
        
        // Делегация в доменный сервис
        wishlistItemRemoveDomainService.removeWishlistItem(userId, itemId);
    }

    private void validateUserExists(Long userId) {
        userService.getUserById(userId).orElseThrow(() -> {
            log.warn("User not found for userId: {}", userId);
            return new IllegalArgumentException("User not found with id: " + userId);
        });
    }
}
