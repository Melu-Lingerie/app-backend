package ru.melulingerie.facade.cart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.facade.cart.service.CartRemoveItemFacadeService;
import ru.melulingerie.cart.service.CartRemoveItemService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartRemoveItemFacadeServiceImpl implements CartRemoveItemFacadeService {

    private final CartRemoveItemService cartRemoveItemService;

    @Override
    public void removeItemsFromCart(Long cartId, List<Long> itemIds) {
        log.debug("Removing items from cart: {}, itemIds: {}", cartId, itemIds);
        
        cartRemoveItemService.removeCartItems(cartId, itemIds);
    }
}