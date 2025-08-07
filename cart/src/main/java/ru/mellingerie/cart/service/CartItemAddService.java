package ru.mellingerie.cart.service;

import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mellingerie.cart.dto.AddToCartRequest;
import ru.mellingerie.cart.dto.AddToCartResponse;
import ru.mellingerie.cart.entity.Cart;
import ru.mellingerie.cart.entity.CartItem;
import ru.mellingerie.cart.exception.ProductNotAvailableException;
import ru.mellingerie.cart.repository.CartItemRepository;
import ru.mellingerie.cart.repository.CartRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartItemAddService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartValidationService cartValidationService;

    @Transactional
    public AddToCartResponse addCartItemToCart(Long userId, AddToCartRequest request) {
        cartValidationService.validateAddToCartRequest(request);

        Cart cart = findOrCreateActiveCart(userId);

        Optional<CartItem> existingItemOpt = cartItemRepository.findExistingItem(
                cart.getId(), request.productId(), request.variantId());

        int finalQuantity = existingItemOpt.map(CartItem::getQuantity)
                .orElse(0) + request.quantity();

        CartItem savedItem = existingItemOpt
                .map(ci -> {
                    ci.setQuantity(finalQuantity);
                    ci.setProductPriceId(request.productPriceId());
                    return ci;
                })
                .orElseGet(() -> CartItem.builder()
                        .cartId(cart.getId())
                        .productId(request.productId())
                        .variantId(request.variantId())
                        .quantity(finalQuantity)
                        .productPriceId(request.productPriceId())
                        .build());

        try {
            cartItemRepository.save(savedItem);
        } catch (DataIntegrityViolationException | PersistenceException ex) {
            log.warn("Failed to add product to cart due to missing references: userId={}, productId={}, variantId={}, priceId={}",
                    userId, request.productId(), request.variantId(), request.productPriceId(), ex);
            throw new ProductNotAvailableException(request.productId(), request.variantId());
        }

        cartRepository.updateCartTimestamp(cart.getId());

        return AddToCartResponse.builder()
                .cartItemId(savedItem.getId())
                .finalQuantity(finalQuantity)
                .isNewItem(existingItemOpt.isEmpty())
                .message("Item added to cart")
                .build();
    }

    private Cart findOrCreateActiveCart(Long userId) {
        return cartRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().userId(userId).isActive(true).build();
                    log.debug("Created new cart for user {}", userId);
                    return cartRepository.save(newCart);
                });
    }
}
