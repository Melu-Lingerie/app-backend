package ru.mellingerie.cart.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mellingerie.cart.dto.CartView;
import ru.mellingerie.cart.entity.Cart;
import ru.mellingerie.cart.entity.CartItem;
import ru.mellingerie.cart.repository.CartItemRepository;
import ru.mellingerie.cart.repository.CartRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartQueryService Unit Tests")
class CartQueryServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;

    private CartQueryService cartQueryService;

    @BeforeEach
    void setUp() {
        cartQueryService = new CartQueryService(cartRepository, cartItemRepository);
    }

    @Test
    @DisplayName("Should return a populated CartView for a user with a cart")
    void shouldReturnPopulatedCartView() {
        // given
        Long userId = 1L;
        Long cartId = 1L;
        Cart cart = Cart.builder().id(cartId).userId(userId).isActive(true).updatedAt(LocalDateTime.now()).build();
        List<CartItem> items = List.of(
                CartItem.builder().id(10L).cartId(cartId).quantity(2).build(),
                CartItem.builder().id(11L).cartId(cartId).quantity(3).build()
        );

        when(cartRepository.findByUserIdAndIsActiveTrue(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findAllByCartId(cartId)).thenReturn(items);

        // when
        Optional<CartView> cartViewOpt = cartQueryService.getCart(userId);

        // then
        assertThat(cartViewOpt).isPresent();
        CartView cartView = cartViewOpt.get();
        assertThat(cartView.cartId()).isEqualTo(cartId);
        assertThat(cartView.userId()).isEqualTo(userId);
        assertThat(cartView.items()).hasSize(2);
        assertThat(cartView.itemsCount()).isEqualTo(2);
        assertThat(cartView.totalQuantity()).isEqualTo(5); // 2 + 3
    }

    @Test
    @DisplayName("Should return an empty Optional for a user without a cart")
    void shouldReturnEmptyOptionalForUserWithoutCart() {
        // given
        Long userId = 99L;
        when(cartRepository.findByUserIdAndIsActiveTrue(userId)).thenReturn(Optional.empty());

        // when
        Optional<CartView> cartViewOpt = cartQueryService.getCart(userId);

        // then
        assertThat(cartViewOpt).isNotPresent();
    }
}