package ru.mellingerie.cart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mellingerie.cart.dto.UpdateCartItemRequest;
import ru.mellingerie.cart.entity.CartItem;
import ru.mellingerie.cart.exception.CartItemNotFoundException;
import ru.mellingerie.cart.repository.CartItemRepository;
import ru.mellingerie.cart.repository.CartRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartItemQuantityUpdateService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartValidationService cartValidationService;

    public void updateCartItemQuantity(Long userId, Long cartItemId, UpdateCartItemRequest request) {
        log.info("Updating cart item quantity for user {}: itemId={}, quantity={}", userId, cartItemId, request.quantity());

        cartValidationService.validateQuantity(request.quantity());
        cartValidationService.validateCartItemExists(cartItemId, userId);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId));

        cartItemRepository.updateCartItemQuantity(item.getId(), request.quantity(), request.productPriceId());
        int updated = cartRepository.updateCartTimestamp(item.getCartId());
        if (updated == 0) {
            log.warn("Cart timestamp was not updated for cartId={} after quantity change (userId={}, cartItemId={})",
                    item.getCartId(), userId, cartItemId);
        } else if (log.isDebugEnabled()) {
            log.debug("Cart timestamp updated for cartId={} after quantity change", item.getCartId());
        }
    }
}