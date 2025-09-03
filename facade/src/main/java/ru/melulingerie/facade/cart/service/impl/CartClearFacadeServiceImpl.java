package ru.melulingerie.facade.cart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ru.melulingerie.facade.cart.service.CartClearFacadeService;
import ru.melulingerie.service.CartClearService;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartClearFacadeServiceImpl implements CartClearFacadeService {

    private final CartClearService cartClearService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public Integer clearCart(Long cartId) {
        log.debug("Clearing cart: {}", cartId);
        
        return transactionTemplate.execute(status ->
                cartClearService.clearCart(cartId)
        );
    }
}