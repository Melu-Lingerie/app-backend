package ru.mellingerie.cart.service;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mellingerie.cart.dto.UpdateCartItemRequest;
import ru.mellingerie.cart.entity.CartItem;
import ru.mellingerie.cart.exception.CartItemNotFoundException;
import ru.mellingerie.cart.repository.CartItemRepository;
import ru.mellingerie.cart.repository.CartRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartItemQuantityUpdateService Unit Tests")
class CartItemQuantityUpdateServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;

    private CartItemQuantityUpdateService cartItemQuantityUpdateService;

    @BeforeEach
    void setUp() {
        CartValidationService cartValidationService = new CartValidationService(cartRepository);
        cartItemQuantityUpdateService = new CartItemQuantityUpdateService(cartRepository, cartItemRepository, cartValidationService);
    }

    @Test
    @DisplayName("Should update item quantity successfully")
    void shouldUpdateItemQuantitySuccessfully() {
        // given
        Long userId = 1L;
        Long cartId = 10L;
        Long cartItemId = 100L;
        UpdateCartItemRequest request = new UpdateCartItemRequest(5, 301L);
        CartItem itemToUpdate = CartItem.builder().id(cartItemId).cartId(cartId).build();

        when(cartRepository.cartItemExistsAndBelongsToUser(cartItemId, userId)).thenReturn(true);
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(itemToUpdate));

        // when
        cartItemQuantityUpdateService.updateCartItemQuantity(userId, cartItemId, request);

        // then
        verify(cartItemRepository).updateCartItemQuantity(cartItemId, 5, 301L);
        verify(cartRepository).updateCartTimestamp(cartId);
        verifyNoMoreInteractions(cartItemRepository, cartRepository);
    }

    @Test
    @DisplayName("Should throw CartItemNotFoundException if item does not belong to user")
    void shouldThrowExceptionWhenItemNotBelongsToUser() {
        // given
        Long userId = 1L;
        Long otherUsersCartItemId = 200L;
        UpdateCartItemRequest request = new UpdateCartItemRequest(5, 301L);

        when(cartRepository.cartItemExistsAndBelongsToUser(otherUsersCartItemId, userId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> cartItemQuantityUpdateService.updateCartItemQuantity(userId, otherUsersCartItemId, request))
                .isInstanceOf(CartItemNotFoundException.class)
                // ИСПРАВЛЕНО: Проверяем точное сообщение, которое генерируется исключением
                .hasMessage("Cart item with id " + otherUsersCartItemId + " and with userId " + userId + " not found");

        verify(cartItemRepository, never()).updateCartItemQuantity(anyLong(), anyInt(), anyLong());
    }

    @Test
    @DisplayName("Should throw ValidationException for invalid quantity")
    void shouldThrowExceptionForInvalidQuantity() {
        // given
        Long userId = 1L;
        Long cartItemId = 100L;
        UpdateCartItemRequest request = new UpdateCartItemRequest(0, 301L);

        // when & then
        assertThatThrownBy(() -> cartItemQuantityUpdateService.updateCartItemQuantity(userId, cartItemId, request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Quantity must be a positive integer.");

        verify(cartRepository, never()).cartItemExistsAndBelongsToUser(anyLong(), anyLong());
        verify(cartItemRepository, never()).updateCartItemQuantity(anyLong(), anyInt(), anyLong());
    }
}