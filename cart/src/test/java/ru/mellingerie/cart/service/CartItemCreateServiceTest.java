package ru.mellingerie.cart.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.mellingerie.cart.dto.AddToCartRequest;
import ru.mellingerie.cart.entity.Cart;
import ru.mellingerie.cart.entity.CartItem;
import ru.mellingerie.cart.exception.ProductNotAvailableException;
import ru.mellingerie.cart.repository.CartItemRepository;
import ru.mellingerie.cart.repository.CartRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartItemAddService (Create) Unit Tests")
class CartItemCreateServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartValidationService cartValidationService;

    private CartItemAddService service;

    @BeforeEach
    void setUp() {
        service = new CartItemAddService(cartRepository, cartItemRepository, cartValidationService);
    }

    @Test
    @DisplayName("Увеличивает количество для существующей позиции и обновляет timestamp корзины")
    void addCartItem_incrementsQuantityForExistingItem() {
        // given
        Long userId = 1L;
        Long cartId = 100L;
        Long productId = 10L;
        Long variantId = 20L;
        Long priceId = 1000L;
        int existingQty = 2;
        int toAdd = 3;

        AddToCartRequest request = mock(AddToCartRequest.class);
        when(request.productId()).thenReturn(productId);
        when(request.variantId()).thenReturn(variantId);
        when(request.productPriceId()).thenReturn(priceId);
        when(request.quantity()).thenReturn(toAdd);

        Cart cart = mock(Cart.class);
        when(cart.getId()).thenReturn(cartId);
        when(cartRepository.findByUserIdAndIsActiveTrue(userId)).thenReturn(Optional.of(cart));

        CartItem existingItem = mock(CartItem.class);
        when(existingItem.getQuantity()).thenReturn(existingQty);
        when(existingItem.getId()).thenReturn(555L);
        when(cartItemRepository.findExistingItem(cartId, productId, variantId)).thenReturn(Optional.of(existingItem));

        // when
        var response = service.addCartItemToCart(userId, request);

        // then
        assertNotNull(response, "Ответ не должен быть null");
        assertEquals(existingQty + toAdd, response.finalQuantity(), "Должно увеличиться итоговое количество");
        assertFalse(response.isNewItem(), "Позиция должна считаться существующей");

        verify(cartValidationService, times(1)).validateAddToCartRequest(request);
        verify(existingItem, times(1)).setQuantity(existingQty + toAdd);
        verify(existingItem, times(1)).setProductPriceId(priceId);
        verify(cartItemRepository, times(1)).save(existingItem);
        verify(cartRepository, times(1)).updateCartTimestamp(cartId);
    }

    @Test
    @DisplayName("Создает новую позицию, если такой еще нет, и обновляет timestamp корзины")
    void addCartItem_createsNewItemWhenAbsent() {
        // given
        Long userId = 2L;
        Long cartId = 200L;
        Long productId = 11L;
        Long variantId = 21L;
        Long priceId = 2000L;
        int toAdd = 4;

        AddToCartRequest request = mock(AddToCartRequest.class);
        when(request.productId()).thenReturn(productId);
        when(request.variantId()).thenReturn(variantId);
        when(request.productPriceId()).thenReturn(priceId);
        when(request.quantity()).thenReturn(toAdd);

        Cart cart = mock(Cart.class);
        when(cart.getId()).thenReturn(cartId);
        when(cartRepository.findByUserIdAndIsActiveTrue(userId)).thenReturn(Optional.of(cart));

        when(cartItemRepository.findExistingItem(cartId, productId, variantId)).thenReturn(Optional.empty());

        // Захватим аргумент сохраняемой сущности, чтобы убедиться, что количество установлено корректно
        ArgumentCaptor<CartItem> captor = ArgumentCaptor.forClass(CartItem.class);
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        var response = service.addCartItemToCart(userId, request);

        // then
        assertNotNull(response, "Ответ не должен быть null");
        assertEquals(toAdd, response.finalQuantity(), "Для новой позиции итоговое количество равно добавляемому");
        assertTrue(response.isNewItem(), "Позиция должна считаться новой");

        verify(cartValidationService, times(1)).validateAddToCartRequest(request);
        verify(cartItemRepository, times(1)).save(captor.capture());
        verify(cartRepository, times(1)).updateCartTimestamp(cartId);

        CartItem saved = captor.getValue();
        assertNotNull(saved, "Сохраняемая сущность не должна быть null");
        // Проверяем только количество и priceId, чтобы не зависеть от конкретных геттеров полей id'ов
        assertEquals(toAdd, saved.getQuantity(), "Количество в новой позиции должно быть установлено");
        assertEquals(priceId, saved.getProductPriceId(), "PriceId в новой позиции должен быть установлен");
    }

    @Test
    @DisplayName("Бросает ProductNotAvailableException при ошибке сохранения позиции")
    void addCartItem_throwsWhenSaveFails() {
        // given
        Long userId = 3L;
        Long cartId = 300L;
        Long productId = 12L;
        Long variantId = 22L;
        Long priceId = 3000L;
        int toAdd = 1;

        AddToCartRequest request = mock(AddToCartRequest.class);
        when(request.productId()).thenReturn(productId);
        when(request.variantId()).thenReturn(variantId);
        when(request.productPriceId()).thenReturn(priceId);
        when(request.quantity()).thenReturn(toAdd);

        Cart cart = mock(Cart.class);
        when(cart.getId()).thenReturn(cartId);
        when(cartRepository.findByUserIdAndIsActiveTrue(userId)).thenReturn(Optional.of(cart));

        CartItem existingItem = mock(CartItem.class);
        when(existingItem.getQuantity()).thenReturn(0);
        when(cartItemRepository.findExistingItem(cartId, productId, variantId)).thenReturn(Optional.of(existingItem));

        doThrow(new DataIntegrityViolationException("fk violation"))
                .when(cartItemRepository).save(any(CartItem.class));

        // when - then
        assertThrows(ProductNotAvailableException.class, () -> service.addCartItemToCart(userId, request));
        verify(cartRepository, never()).updateCartTimestamp(anyLong());
    }
}
