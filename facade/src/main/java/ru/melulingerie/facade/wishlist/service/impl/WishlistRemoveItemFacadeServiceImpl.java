package ru.melulingerie.facade.wishlist.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ru.melulingerie.facade.wishlist.service.WishlistRemoveItemFacadeService;
import ru.melulingerie.service.WishlistRemoveItemService;

import java.util.List;

/**
 * Фасадный сервис для удаления элементов из wishlist
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistRemoveItemFacadeServiceImpl implements WishlistRemoveItemFacadeService {

    private final TransactionTemplate transactionTemplate;
    private final WishlistRemoveItemService wishlistRemoveItemService;

    @Override
    public void removeItemsFromWishlist(Long wishlistId, List<Long> wishlistItemIds) {
        transactionTemplate.executeWithoutResult(status -> 
            wishlistRemoveItemService.removeWishlistItems(wishlistId, wishlistItemIds));
    }
}