package ru.melulingerie.cart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.cart.domain.CartItem;
import ru.melulingerie.cart.dto.request.CartUpdateQuantityRequestDto;
import ru.melulingerie.cart.repository.CartItemRepository;
import ru.melulingerie.cart.service.CartUpdateQuantityService;


@Slf4j
@Service
@RequiredArgsConstructor
public class CartUpdateQuantityServiceImpl implements CartUpdateQuantityService {

    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateItemQuantity(Long cartId, Long itemId, CartUpdateQuantityRequestDto request) {
        log.debug("Updating quantity for cartId: {}, itemId: {}, quantity: {}",
                cartId, itemId, request.quantity());

        CartItem cartItem = cartItemRepository.findActiveByCartIdAndItemId(cartId, itemId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cart item not found. CartId: " + cartId + ", ItemId: " + itemId));

        cartItem.updateQuantity(request.quantity());

        cartItemRepository.save(cartItem);
    }

}