package ru.melulingerie.cart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.cart.domain.Cart;
import ru.melulingerie.cart.domain.CartItem;
import ru.melulingerie.cart.repository.CartItemRepository;
import ru.melulingerie.cart.repository.CartRepository;
import ru.melulingerie.cart.service.CartClearService;

import java.util.List;


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

        Cart cart = cartRepository.findCartByIdWithItemsSortedByDate(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found with id: " + cartId));

        int deletedCount = cart.getCartItems().size();
        
        if (deletedCount > 0) {
            cartItemRepository.deleteAll(cart.getCartItems());
            cartRepository.save(cart);
        }

        log.info("Cleared {} items from cart {}", deletedCount, cartId);

        return deletedCount;
    }
}