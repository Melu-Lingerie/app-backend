package ru.melulingerie.facade.wishlist.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ru.melulingerie.facade.wishlist.service.WishlistCreateFacadeService;
import ru.melulingerie.service.WishlistCreateService;
import ru.melulingerie.users.service.UserCreateService;

/**
 * Фасадный сервис для создания wishlist
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistCreateFacadeServiceImpl implements WishlistCreateFacadeService {

    private final WishlistCreateService wishlistCreateService;
    private final UserCreateService userCreateService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public Long createWishlistForUser(Long userId) {
        validateUserExists(userId);
        
        return transactionTemplate.execute(status -> wishlistCreateService.createWishlistForUser(userId));
    }

    private void validateUserExists(Long userId) {
        userCreateService.getUserById(userId).orElseThrow(() -> {
            log.warn("User not found for userId: {}", userId);
            return new IllegalArgumentException("User not found with id: " + userId);
        });
    }
}