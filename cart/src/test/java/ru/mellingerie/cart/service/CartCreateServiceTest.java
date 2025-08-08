package ru.mellingerie.cart.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mellingerie.cart.entity.Cart;
import ru.mellingerie.cart.repository.CartRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartCreateService Unit Tests")
class CartCreateServiceTest {

    @Mock
    private CartRepository cartRepository;

    private CartCreateService cartCreateService;

    @BeforeEach
    void setUp() {
        cartCreateService = new CartCreateService(cartRepository);
    }

    @Test
    @DisplayName("Возвращает существующую активную корзину, если она уже есть")
    void shouldReturnExistingCartWhenActiveExists() {
        // given
        Long userId = 42L;
        Cart existingCart = mock(Cart.class);
        when(existingCart.getId()).thenReturn(100L);
        when(cartRepository.findByUserIdAndIsActiveTrue(userId)).thenReturn(Optional.of(existingCart));

        // when
        var response = cartCreateService.createCart(userId);

        // then
        assertFalse(response.isNewCart(), "Ожидается флаг isNewCart=false для существующей корзины");
        verify(cartRepository, times(1)).findByUserIdAndIsActiveTrue(userId);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("Создает новую корзину, если активная отсутствует")
    void shouldCreateNewCartWhenNoActiveCart() {
        // given
        Long userId = 99L;
        when(cartRepository.findByUserIdAndIsActiveTrue(userId)).thenReturn(Optional.empty());

        Cart savedCart = mock(Cart.class);
        when(savedCart.getId()).thenReturn(101L);
        when(cartRepository.save(any(Cart.class))).thenReturn(savedCart);

        // when
        var response = cartCreateService.createCart(userId);

        // then
        assertTrue(response.isNewCart(), "Ожидается флаг isNewCart=true при создании новой корзины");
        verify(cartRepository, times(1)).findByUserIdAndIsActiveTrue(userId);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }
}