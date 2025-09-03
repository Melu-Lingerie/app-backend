package ru.melulingerie.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.cart.domain.Cart;
import ru.melulingerie.cart.domain.CartItem;
import ru.melulingerie.cart.repository.CartItemRepository;
import ru.melulingerie.cart.repository.CartRepository;
import ru.melulingerie.service.CartRemoveItemService;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartRemoveItemServiceImpl implements CartRemoveItemService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void removeCartItems(Long cartId, List<Long> itemIds) {
        log.debug("Removing items from cart: {}, itemIds: {}", cartId, itemIds);
        
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found with id: " + cartId));
        
        int deletedCount = cartItemRepository.softDeleteByCartIdAndItemIds(cartId, itemIds);
        
        if (deletedCount == 0) {
            log.warn("No items were deleted for cartId: {}, itemIds: {}", cartId, itemIds);
        }
        
        updateCartTotalAmount(cart);
        
        log.info("Removed {} items from cart {}", deletedCount, cartId);
    }

    private void updateCartTotalAmount(Cart cart) {
        Cart updatedCart = cartRepository.findByIdWithActiveItems(cart.getId())
                .orElseThrow(() -> new IllegalArgumentException("Cart not found with id: " + cart.getId()));
        
        BigDecimal totalAmount = updatedCart.getCartItems().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        updatedCart.setTotalAmount(totalAmount);
        cartRepository.save(updatedCart);
    }
}