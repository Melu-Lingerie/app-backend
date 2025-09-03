package ru.melulingerie.facade.cart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ru.melulingerie.facade.cart.service.CartRemoveItemFacadeService;
import ru.melulingerie.service.CartRemoveItemService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartRemoveItemFacadeServiceImpl implements CartRemoveItemFacadeService {

    private final CartRemoveItemService cartRemoveItemService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public void removeItemsFromCart(Long cartId, List<Long> itemIds) {
        validateItemIds(itemIds);
        
        log.debug("Removing items from cart: {}, itemIds: {}", cartId, itemIds);
        
        transactionTemplate.executeWithoutResult(status ->
                cartRemoveItemService.removeCartItems(cartId, itemIds)
        );
    }

    private void validateItemIds(List<Long> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            log.warn("Item IDs list is empty or null");
            throw new IllegalArgumentException("Item IDs list cannot be empty");
        }
    }
}