package ru.mellingerie.cart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mellingerie.cart.entity.CartItem;
import ru.mellingerie.cart.exception.CartItemNotFoundException;
import ru.mellingerie.cart.repository.CartItemRepository;
import ru.mellingerie.cart.repository.CartRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartItemRemoveService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartValidationService cartValidationService;

    public void removeCartItem(Long userId, Long cartItemId) {
        log.info("Removing cart item for user {}: itemId={}", userId, cartItemId);
        cartValidationService.validateCartItemExists(cartItemId, userId);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId));

        cartItemRepository.deleteById(item.getId());
        int updated = cartRepository.updateCartTimestamp(item.getCartId());
        if (updated == 0) {
            log.warn("Cart timestamp was not updated for cartId={} after item removal (userId={}, cartItemId={})",
                    item.getCartId(), userId, cartItemId);
        } else if (log.isDebugEnabled()) {
            log.debug("Cart timestamp updated for cartId={} after item removal", item.getCartId());
        }
    }
}