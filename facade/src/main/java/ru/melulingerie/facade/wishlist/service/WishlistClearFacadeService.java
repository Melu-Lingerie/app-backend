package ru.melulingerie.facade.wishlist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import ru.melulingerie.facade.wishlist.mocks.UserService;
import ru.melulingerie.service.WishlistClearDomainService;

/**
 * Фасадный сервис для очистки wishlist
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistClearFacadeService {

    private final WishlistClearDomainService wishlistClearDomainService;
    private final UserService userService;
    private final PlatformTransactionManager transactionManager;

    public void clearWishlist(Long userId) {
        validateUserExists(userId);
        
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        tx.executeWithoutResult(status -> wishlistClearDomainService.clearWishlist(userId));
    }

    private void validateUserExists(Long userId) {
        userService.getUserById(userId).orElseThrow(() -> {
            log.warn("User not found for userId: {}", userId);
            return new IllegalArgumentException("User not found with id: " + userId);
        });
    }
}