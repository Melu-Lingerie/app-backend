package ru.mellingerie.cart.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mellingerie.cart.dto.ClearCartResponse;
import ru.mellingerie.cart.entity.Cart;
import ru.mellingerie.cart.repository.CartItemRepository;
import ru.mellingerie.cart.repository.CartRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartClearService Unit Tests")
class CartClearServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;

    private CartClearService cartClearService;

    @BeforeEach
    void setUp() {
        cartClearService = new CartClearService(cartRepository, cartItemRepository);
    }

    @Test
    @DisplayName("Should clear a cart with items")
    void shouldClearCartWithItems() {
        // given
        Long userId = 1L;
        Long cartId = 1L;
        Cart cart = Cart.builder().id(cartId).userId(userId).build();
        when(cartRepository.findByUserIdAndIsActiveTrue(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.countByCartId(cartId)).thenReturn(5L);

        // when
        ClearCartResponse response = cartClearService.clearCart(userId);

        // then
        verify(cartItemRepository).deleteAllByCartId(cartId);
        verify(cartRepository).updateCartTimestamp(cartId);
        assertThat(response.itemsRemoved()).isEqualTo(5);
        assertThat(response.status()).isEqualTo("SUCCESS");
        assertThat(response.cartId()).isEqualTo(cartId);
    }

    @Test
    @DisplayName("Should handle clearing an already empty cart")
    void shouldHandleClearingEmptyCart() {
        // given
        Long userId = 2L;
        Long cartId = 2L;
        Cart cart = Cart.builder().id(cartId).userId(userId).build();
        when(cartRepository.findByUserIdAndIsActiveTrue(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.countByCartId(cartId)).thenReturn(0L);

        // when
        ClearCartResponse response = cartClearService.clearCart(userId);

        // then
        verify(cartItemRepository, never()).deleteAllByCartId(anyLong());
        verify(cartRepository, never()).updateCartTimestamp(anyLong());
        assertThat(response.itemsRemoved()).isZero();
        assertThat(response.status()).isEqualTo("SUCCESS");
    }

    @Test
    @DisplayName("Should handle clearing for a user with no cart")
    void shouldHandleClearingNonExistentCart() {
        // given
        Long userId = 3L;
        when(cartRepository.findByUserIdAndIsActiveTrue(userId)).thenReturn(Optional.empty());

        // when
        ClearCartResponse response = cartClearService.clearCart(userId);

        // then
        assertThat(response.itemsRemoved()).isZero();
        assertThat(response.status()).isEqualTo("NO_CART");
        assertThat(response.cartId()).isNull();
    }
}