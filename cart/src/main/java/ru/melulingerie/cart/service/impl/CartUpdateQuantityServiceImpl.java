package ru.melulingerie.cart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.cart.domain.CartItem;
import ru.melulingerie.cart.repository.CartItemRepository;
import ru.melulingerie.cart.service.CartUpdateQuantityService;


@Slf4j
@Service
@RequiredArgsConstructor
public class CartUpdateQuantityServiceImpl implements CartUpdateQuantityService {

    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateItemQuantity(Long cartId, Long itemId, Integer quantity) {
        log.debug("Updating quantity for cartId: {}, itemId: {}, quantity: {}",
                cartId, itemId, quantity);

        CartItem cartItem = cartItemRepository.findByCartIdAndItemId(cartId, itemId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cart item not found. CartId: " + cartId + ", ItemId: " + itemId));

        cartItem.updateQuantity(quantity);

        cartItemRepository.save(cartItem);
    }

}