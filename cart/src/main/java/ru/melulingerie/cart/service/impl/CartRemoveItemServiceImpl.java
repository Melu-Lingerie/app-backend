package ru.melulingerie.cart.service.impl;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.cart.domain.Cart;
import ru.melulingerie.cart.domain.CartItem;
import ru.melulingerie.cart.repository.CartItemRepository;
import ru.melulingerie.cart.repository.CartRepository;
import ru.melulingerie.cart.service.CartRemoveItemService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartRemoveItemServiceImpl implements CartRemoveItemService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeCartItems(Long cartId, List<Long> itemIds) {
        log.debug("Removing items from cart: {}, itemIds: {}", cartId, itemIds);
        
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found with id: " + cartId));
        
        int deletedCount = cartItemRepository.deleteByCartIdAndItemIds(cartId, itemIds);
        
        if (deletedCount > 0) {
            cartRepository.save(cart);
        }
        
        if (deletedCount == 0) {
            log.warn("No items were deleted for cartId: {}, itemIds: {}", cartId, itemIds);
        }
        
        log.info("Removed {} items from cart {}", deletedCount, cartId);
    }
}