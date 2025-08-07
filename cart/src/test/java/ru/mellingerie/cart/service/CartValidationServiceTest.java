
package ru.mellingerie.cart.service;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mellingerie.cart.dto.AddToCartRequest;
import ru.mellingerie.cart.exception.CartItemNotFoundException;
import ru.mellingerie.cart.repository.CartRepository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartValidationService Unit Tests")
class CartValidationServiceTest {

    @Mock
    private CartRepository cartRepository;

    private CartValidationService cartValidationService;

    @BeforeEach
    void setUp() {
        // Создаем тестируемый сервис с моком
        cartValidationService = new CartValidationService(cartRepository);
    }

    // Тесты для validateAddToCartRequest
    @Test
    @DisplayName("Should pass validation for a valid AddToCartRequest")
    void shouldPassValidationForValidRequest() {
        // given
        AddToCartRequest validRequest = new AddToCartRequest(101L, 201L, 1, 301L);

        // when & then
        // Проверяем, что исключение не было брошено
        assertDoesNotThrow(() -> cartValidationService.validateAddToCartRequest(validRequest));
    }

    @Test
    @DisplayName("Should throw ValidationException if productId is null")
    void shouldThrowExceptionWhenProductIdIsNull() {
        // given
        AddToCartRequest invalidRequest = new AddToCartRequest(null, 201L, 1, 301L);

        // when & then
        assertThatThrownBy(() -> cartValidationService.validateAddToCartRequest(invalidRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Product, Variant, and Price IDs must not be null");
    }

    // Тесты для validateQuantity
    @Test
    @DisplayName("Should throw ValidationException if quantity is zero")
    void shouldThrowExceptionWhenQuantityIsZero() {
        // when & then
        assertThatThrownBy(() -> cartValidationService.validateQuantity(0))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Quantity must be a positive integer.");
    }

    @Test
    @DisplayName("Should throw ValidationException if quantity is null")
    void shouldThrowExceptionWhenQuantityIsNull() {
        // when & then
        assertThatThrownBy(() -> cartValidationService.validateQuantity(null))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Quantity must be a positive integer.");
    }

    // Тесты для validateCartItemExists
    @Test
    @DisplayName("Should pass validation if item exists and belongs to user")
    void shouldPassWhenItemExistsAndBelongsToUser() {
        // given
        Long userId = 1L;
        Long cartItemId = 10L;
        when(cartRepository.cartItemExistsAndBelongsToUser(cartItemId, userId)).thenReturn(true);

        // when & then
        assertDoesNotThrow(() -> cartValidationService.validateCartItemExists(cartItemId, userId));
        // Убеждаемся, что метод репозитория был вызван
        verify(cartRepository).cartItemExistsAndBelongsToUser(cartItemId, userId);
    }

    @Test
    @DisplayName("Should throw CartItemNotFoundException if item does not exist or belong to user")
    void shouldThrowWhenItemDoesNotExist() {
        // given
        Long userId = 1L;
        Long cartItemId = 10L;
        // Настраиваем мок так, как будто товар не принадлежит пользователю
        when(cartRepository.cartItemExistsAndBelongsToUser(cartItemId, userId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> cartValidationService.validateCartItemExists(cartItemId, userId))
                .isInstanceOf(CartItemNotFoundException.class)
                // ИСПРАВЛЕНО: Проверяем точное сообщение об ошибке
                .hasMessage("Cart item with id 10 and with userId 1 not found");
    }
}