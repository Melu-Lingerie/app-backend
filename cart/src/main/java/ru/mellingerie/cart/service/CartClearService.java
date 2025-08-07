package ru.mellingerie.cart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mellingerie.cart.dto.ClearCartResponse;
import ru.mellingerie.cart.repository.CartItemRepository;
import ru.mellingerie.cart.repository.CartRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartClearService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public ClearCartResponse clearCart(Long userId) {
        log.info("Clearing cart for user {}", userId);

        return cartRepository.findByUserIdAndIsActiveTrue(userId)
                .map(cart -> {
                    long itemsRemoved = cartItemRepository.countByCartId(cart.getId());
                    if (itemsRemoved > 0) {
                        cartItemRepository.deleteAllByCartId(cart.getId());
                        cartRepository.updateCartTimestamp(cart.getId());
                    }

                    return new ClearCartResponse(
                            userId,
                            cart.getId(),
                            (int) itemsRemoved,
                            null, // totalValueRemoved - не рассчитывается в текущей логике
                            "Cart successfully cleared",
                            LocalDateTime.now(),
                            "SUCCESS"
                    );
                })
                .orElseGet(() -> new ClearCartResponse(
                        userId,
                        null, // cartId
                        0,
                        null, // totalValueRemoved
                        "No active cart found for user",
                        LocalDateTime.now(),
                        "NO_CART"
                ));
    }
}