package ru.melulingerie.facade.wishlist.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ru.melulingerie.facade.wishlist.mocks.UserService;
import ru.melulingerie.facade.wishlist.service.WishlistClearFacadeService;
import ru.melulingerie.service.WishlistClearService;

/**
 * Фасадный сервис для очистки wishlist
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistClearFacadeServiceImpl implements WishlistClearFacadeService {

    private final WishlistClearService wishlistClearService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public int clearWishlist(Long wishlistId) {
        Integer result = transactionTemplate.execute(status -> wishlistClearService.clearWishlist(wishlistId));
        return result != null ? result : 0;
    }

}