package ru.mellingerie.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mellingerie.cart.dto.CartItemView;
import ru.mellingerie.cart.dto.CartView;
import ru.mellingerie.cart.entity.Cart;
import ru.mellingerie.cart.entity.CartItem;
import ru.mellingerie.cart.repository.CartItemRepository;
import ru.mellingerie.cart.repository.CartRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartQueryService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public Optional<CartView> getCart(Long userId) {
        return cartRepository.findByUserIdAndIsActiveTrue(userId)
                .map(this::createCartView);
    }

    private CartView createCartView(Cart cart) {
        List<CartItem> items = cartItemRepository.findAllByCartId(cart.getId());
        List<CartItemView> itemViews = items.stream()
                .map(this::convertToCartItemView)
                .collect(Collectors.toList());

        return CartView.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .items(itemViews)
                .itemsCount(itemViews.size())
                .totalQuantity(itemViews.stream().mapToInt(CartItemView::quantity).sum())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }

    private CartItemView convertToCartItemView(CartItem item) {
        return CartItemView.builder()
                .itemId(item.getId())
                .productId(item.getProductId())
                .variantId(item.getVariantId())
                .quantity(item.getQuantity())
                .productPriceId(item.getProductPriceId())
                .addedAt(item.getAddedAt())
                .build();
    }
}