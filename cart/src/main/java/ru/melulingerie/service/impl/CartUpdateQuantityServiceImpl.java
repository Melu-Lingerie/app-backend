package ru.melulingerie.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.cart.domain.Cart;
import ru.melulingerie.cart.domain.CartItem;
import ru.melulingerie.cart.repository.CartItemRepository;
import ru.melulingerie.cart.repository.CartRepository;
import ru.melulingerie.dto.CartUpdateQuantityRequestDto;
import ru.melulingerie.service.CartUpdateQuantityService;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartUpdateQuantityServiceImpl implements CartUpdateQuantityService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateItemQuantity(Long cartId, Long itemId, CartUpdateQuantityRequestDto request) {
        log.debug("Updating quantity for cartId: {}, itemId: {}, quantity: {}", 
                  cartId, itemId, request.quantity());
        
        CartItem cartItem = cartItemRepository.findActiveByCartIdAndItemId(cartId, itemId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cart item not found. CartId: " + cartId + ", ItemId: " + itemId));

        cartItem.setQuantity(request.quantity());
        BigDecimal newTotalPrice = cartItem.getUnitPrice().multiply(BigDecimal.valueOf(request.quantity()));
        cartItem.setTotalPrice(newTotalPrice);
        
        cartItemRepository.save(cartItem);
        
        updateCartTotalAmount(cartItem.getCart());
    }

    private void updateCartTotalAmount(Cart cart) {
        BigDecimal totalAmount = cart.getCartItems().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        cart.setTotalAmount(totalAmount);
        cartRepository.save(cart);
    }
}