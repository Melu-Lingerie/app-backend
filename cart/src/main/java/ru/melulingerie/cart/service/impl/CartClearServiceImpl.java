package ru.melulingerie.cart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.cart.repository.CartItemRepository;
import ru.melulingerie.cart.repository.CartRepository;
import ru.melulingerie.cart.service.CartClearService;


@Slf4j
@Service
@RequiredArgsConstructor
public class CartClearServiceImpl implements CartClearService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer clearCart(Long cartId) {
        log.debug("Clearing cart: {}", cartId);

        if (!cartRepository.existsById(cartId)) {
            throw new IllegalArgumentException("Cart not found with id: " + cartId);
        }

        int deletedCount = cartItemRepository.softDeleteAllByCartId(cartId);

        log.info("Cleared {} items from cart {}", deletedCount, cartId);

        return deletedCount;
    }
}