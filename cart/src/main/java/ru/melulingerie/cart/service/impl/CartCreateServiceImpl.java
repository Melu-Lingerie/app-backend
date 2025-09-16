package ru.melulingerie.cart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.cart.domain.Cart;
import ru.melulingerie.cart.dto.response.CartCreateResponseDto;
import ru.melulingerie.cart.repository.CartRepository;
import ru.melulingerie.cart.service.CartCreateService;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartCreateServiceImpl implements CartCreateService {

    private final CartRepository cartRepository;

    @Override
    @Transactional
    public CartCreateResponseDto createCart(Long userId) {
        log.debug("Creating or getting existing cart for user: {}", userId);
        
        return cartRepository.findByUserId(userId)
                .map(cart -> {
                    log.info("Returning existing cart with ID {} for user {}", cart.getId(), userId);
                    return new CartCreateResponseDto(
                            cart.getId(),
                            cart.getUserId(),
                            "Existing cart returned"
                    );
                })
                .orElseGet(() -> createNewCart(userId));
    }

    private CartCreateResponseDto createNewCart(Long userId) {
        Cart cart = new Cart(userId);
        Cart savedCart = cartRepository.save(cart);
        
        log.info("Successfully created new cart with ID {} for user {}", savedCart.getId(), userId);
        
        return new CartCreateResponseDto(
                savedCart.getId(),
                savedCart.getUserId(),
                "Cart successfully created"
        );
    }
}