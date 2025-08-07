package ru.mellingerie.cart.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mellingerie.cart.entity.CartItem;
import ru.mellingerie.cart.exception.CartItemNotFoundException;
import ru.mellingerie.cart.repository.CartItemRepository;
import ru.mellingerie.cart.repository.CartRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartRemoveService Unit Tests")
class CartItemRemoveServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;

    private CartItemRemoveService cartItemRemoveService;

    @BeforeEach
    void setUp() {
        CartValidationService cartValidationService = new CartValidationService(cartRepository);
        cartItemRemoveService = new CartItemRemoveService(cartRepository, cartItemRepository, cartValidationService);
    }

    @Test
    @DisplayName("Should remove an item successfully")
    void shouldRemoveItem() {
        // given
        Long userId = 1L;
        Long cartId = 1L;
        Long cartItemId = 10L;
        CartItem item = CartItem.builder().id(cartItemId).cartId(cartId).build();

        when(cartRepository.cartItemExistsAndBelongsToUser(cartItemId, userId)).thenReturn(true);
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(item));

        // when
        cartItemRemoveService.removeCartItem(userId, cartItemId);

        // then
        verify(cartItemRepository).deleteById(cartItemId);
        verify(cartRepository).updateCartTimestamp(cartId);
    }

    @Test
    @DisplayName("Should throw when removing item that does not belong to user")
    void shouldThrowWhenRemovingItemOfAnotherUser() {
        // given
        Long userId = 2L;
        Long cartItemId = 20L;
        when(cartRepository.cartItemExistsAndBelongsToUser(cartItemId, userId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> cartItemRemoveService.removeCartItem(userId, cartItemId))
                .isInstanceOf(CartItemNotFoundException.class);

        verify(cartItemRepository, never()).deleteById(anyLong());
    }
}