package ru.melulingerie.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.cart.domain.Cart;
import ru.melulingerie.cart.repository.CartItemRepository;
import ru.melulingerie.cart.repository.CartRepository;
import ru.melulingerie.service.CartClearService;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartClearServiceImpl implements CartClearService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Integer clearCart(Long cartId) {
        log.debug("Clearing cart: {}", cartId);
        
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found with id: " + cartId));
        
        int deletedCount = cartItemRepository.softDeleteAllByCartId(cartId);
        
        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepository.save(cart);
        
        log.info("Cleared {} items from cart {}", deletedCount, cartId);
        
        return deletedCount;
    }
}