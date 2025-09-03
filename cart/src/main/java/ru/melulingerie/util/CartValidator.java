package ru.melulingerie.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.melulingerie.dto.CartAddItemRequestDto;
import ru.melulingerie.dto.CartUpdateQuantityRequestDto;
import ru.melulingerie.exception.CartExceptions;

/**
 * Компонент для валидации данных корзины
 */
@Slf4j
@Component
public class CartValidator {

    /**
     * Валидация ID корзины
     */
    public void validateCartId(Long cartId) {
        if (cartId == null || cartId <= 0) {
            log.warn("Invalid cart ID: {}", cartId);
            throw new CartExceptions.InvalidIdException(cartId);
        }
    }

    /**
     * Валидация ID элемента корзины
     */
    public void validateItemId(Long itemId) {
        if (itemId == null || itemId <= 0) {
            log.warn("Invalid item ID: {}", itemId);
            throw new CartExceptions.InvalidIdException(itemId);
        }
    }

    /**
     * Валидация запроса на добавление товара в корзину
     */
    public void validateAddItemRequest(CartAddItemRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("Add item request cannot be null");
        }

        validateProductId(request.productId());
        validateVariantId(request.variantId());
        validateQuantity(request.quantity());
    }

    /**
     * Валидация запроса на обновление количества товара
     */
    public void validateUpdateQuantityRequest(CartUpdateQuantityRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("Update quantity request cannot be null");
        }

        validateQuantity(request.quantity());
    }

    /**
     * Валидация ID продукта
     */
    public void validateProductId(Long productId) {
        if (productId == null || productId <= 0) {
            log.warn("Invalid product ID: {}", productId);
            throw new IllegalArgumentException("Product ID must be positive: " + productId);
        }
    }

    /**
     * Валидация ID варианта продукта
     */
    public void validateVariantId(Long variantId) {
        if (variantId == null || variantId <= 0) {
            log.warn("Invalid variant ID: {}", variantId);
            throw new IllegalArgumentException("Variant ID must be positive: " + variantId);
        }
    }

    /**
     * Валидация количества товара
     */
    public void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            log.warn("Invalid quantity: {}", quantity);
            throw new CartExceptions.InvalidQuantityException(quantity);
        }
    }

    /**
     * Валидация ID пользователя
     */
    public void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            log.warn("Invalid user ID: {}", userId);
            throw new IllegalArgumentException("User ID must be positive: " + userId);
        }
    }
}