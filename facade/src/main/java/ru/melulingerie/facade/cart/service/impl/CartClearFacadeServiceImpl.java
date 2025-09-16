package ru.melulingerie.facade.cart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.facade.cart.service.CartClearFacadeService;
import ru.melulingerie.cart.service.CartClearService;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartClearFacadeServiceImpl implements CartClearFacadeService {

    private final CartClearService cartClearService;

    @Override
    public Integer clearCart(Long cartId) {
        log.debug("Clearing cart: {}", cartId);
        return cartClearService.clearCart(cartId);
    }
}