package ru.melulingerie.facade.cart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.cart.service.CartUpdateQuantityService;
import ru.melulingerie.facade.cart.service.CartUpdateQuantityFacadeService;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartUpdateQuantityFacadeServiceImpl implements CartUpdateQuantityFacadeService {

    private final CartUpdateQuantityService cartUpdateQuantityService;

    @Override
    public void updateItemQuantity(Long cartId, Long itemId, Integer quantity) {
        log.debug("Updating quantity for cartId: {}, itemId: {}, quantity: {}", cartId, itemId, quantity);

        cartUpdateQuantityService.updateItemQuantity(cartId, itemId, quantity);
    }
}