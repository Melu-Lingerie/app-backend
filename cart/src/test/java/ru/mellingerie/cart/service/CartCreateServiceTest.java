package ru.mellingerie.cart.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mellingerie.cart.dto.AddToCartRequest;
import ru.mellingerie.cart.dto.AddToCartResponse;
import ru.mellingerie.cart.entity.Cart;
import ru.mellingerie.cart.entity.CartItem;
import ru.mellingerie.cart.repository.CartItemRepository;
import ru.mellingerie.cart.repository.CartRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartCreateService Unit Tests")
class CartCreateServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;

    private CartCreateService cartCreateService;

    @BeforeEach
    void setUp() {
        // Сервис валидации создается как реальный объект с моком внутри
        CartValidationService cartValidationService = new CartValidationService(cartRepository);
        // Тестируемый сервис создается с моками и реальным сервисом валидации
        cartCreateService = new CartCreateService(cartRepository, cartItemRepository, cartValidationService);
    }

    @Test
    @DisplayName("Should create a new cart and add a new item")
    void shouldCreateNewCartAndAddNewItem() {
        // given
        Long userId = 1L;
        AddToCartRequest request = new AddToCartRequest(101L, 201L, 1, 301L);
        Cart newCart = Cart.builder().id(1L).userId(userId).isActive(true).build();

        when(cartRepository.findByUserIdAndIsActiveTrue(userId)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(newCart);
        when(cartItemRepository.findExistingItem(anyLong(), anyLong(), anyLong())).thenReturn(Optional.empty());

        // when
        AddToCartResponse response = cartCreateService.addToCart(userId, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.isNewItem()).isTrue();
        assertThat(response.finalQuantity()).isEqualTo(1);

        verify(cartRepository).save(any(Cart.class));
        verify(cartItemRepository).save(any(CartItem.class));
        verify(cartRepository).updateCartTimestamp(newCart.getId());
    }

    @Test
    @DisplayName("Should add a new item to an existing cart")
    void shouldAddNewItemToExistingCart() {
        // given
        Long userId = 2L;
        Long cartId = 2L;
        AddToCartRequest request = new AddToCartRequest(102L, 202L, 2, 302L);
        Cart existingCart = Cart.builder().id(cartId).userId(userId).isActive(true).build();

        when(cartRepository.findByUserIdAndIsActiveTrue(userId)).thenReturn(Optional.of(existingCart));
        when(cartItemRepository.findExistingItem(cartId, 102L, 202L)).thenReturn(Optional.empty());

        // when
        cartCreateService.addToCart(userId, request);

        // then
        ArgumentCaptor<CartItem> cartItemCaptor = ArgumentCaptor.forClass(CartItem.class);
        verify(cartItemRepository).save(cartItemCaptor.capture());

        CartItem savedItem = cartItemCaptor.getValue();
        assertThat(savedItem.getCartId()).isEqualTo(cartId);
        assertThat(savedItem.getProductId()).isEqualTo(102L);
        assertThat(savedItem.getQuantity()).isEqualTo(2);

        verify(cartRepository, never()).save(any(Cart.class)); // Новая корзина не создается
        verify(cartRepository).updateCartTimestamp(cartId);
    }

    @Test
    @DisplayName("Should add quantity to an existing item in the cart")
    void shouldAddQuantityToExistingItem() {
        // given
        Long userId = 1L;
        Long cartId = 1L;
        AddToCartRequest request = new AddToCartRequest(101L, 201L, 2, 301L);
        Cart existingCart = Cart.builder().id(cartId).userId(userId).isActive(true).build();
        CartItem existingItem = CartItem.builder()
                .id(1L).cartId(cartId).productId(101L).variantId(201L).quantity(3).build();

        when(cartRepository.findByUserIdAndIsActiveTrue(userId)).thenReturn(Optional.of(existingCart));
        when(cartItemRepository.findExistingItem(cartId, 101L, 201L)).thenReturn(Optional.of(existingItem));

        // when
        AddToCartResponse response = cartCreateService.addToCart(userId, request);

        // then
        assertThat(response.isNewItem()).isFalse();
        assertThat(response.finalQuantity()).isEqualTo(5); // 3 (было) + 2 (добавили)

        ArgumentCaptor<CartItem> cartItemCaptor = ArgumentCaptor.forClass(CartItem.class);
        verify(cartItemRepository).save(cartItemCaptor.capture());
        assertThat(cartItemCaptor.getValue().getQuantity()).isEqualTo(5);
    }
}