package ru.mellingerie.cart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mellingerie.cart.dto.CreateCartResponse;
import ru.mellingerie.cart.entity.Cart;
import ru.mellingerie.cart.repository.CartRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartCreateService {

    private final CartRepository cartRepository;

    public CreateCartResponse createCart(Long userId) {
        return cartRepository.findByUserIdAndIsActiveTrue(userId)
                .map(existing -> {
                    log.debug("Active cart already exists for user {}", userId);
                    return CreateCartResponse.builder()
                            .cartId(existing.getId())
                            .isNewCart(false)
                            .message("Active cart already exists")
                            .build();
                })
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .userId(userId)
                            .isActive(true)
                            .build();
                    newCart = cartRepository.save(newCart);
                    log.debug("Created new cart {} for user {}", newCart.getId(), userId);
                    return CreateCartResponse.builder()
                            .cartId(newCart.getId())
                            .isNewCart(true)
                            .message("Cart created")
                            .build();
                });
    }
}