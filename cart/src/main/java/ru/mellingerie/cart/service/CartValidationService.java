package ru.mellingerie.cart.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mellingerie.cart.dto.AddToCartRequest;
import ru.mellingerie.cart.exception.CartItemNotFoundException;
import ru.mellingerie.cart.repository.CartRepository;

@Service
@RequiredArgsConstructor
public class CartValidationService {

    private final CartRepository cartRepository;

    public void validateAddToCartRequest(AddToCartRequest request) {
        if (request.productId() == null || request.variantId() == null || request.productPriceId() == null) {
            throw new ValidationException("Product, Variant, and Price IDs must not be null");
        }
        validateQuantity(request.quantity());
    }

    public void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new ValidationException("Quantity must be a positive integer.");
        }
    }

    public void validateCartItemExists(Long cartItemId, Long userId) {
        if (!cartRepository.cartItemExistsAndBelongsToUser(cartItemId, userId)) {
            throw new CartItemNotFoundException(cartItemId, userId);
        }
    }
}