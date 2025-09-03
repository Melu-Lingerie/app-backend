package ru.melulingerie.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.cart.domain.Cart;
import ru.melulingerie.cart.repository.CartRepository;
import ru.melulingerie.dto.CartGetResponseDto;
import ru.melulingerie.dto.CartItemGetResponseDto;
import ru.melulingerie.service.CartGetService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartGetServiceImpl implements CartGetService {

    private final CartRepository cartRepository;

    @Override
    public CartGetResponseDto getCart(Long cartId) {
        log.debug("Getting cart for cartId: {}", cartId);
        
        Cart cart = cartRepository.findByIdWithItemsSortedByDate(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found with id: " + cartId));

        List<CartItemGetResponseDto> items = cart.getCartItems().stream()
                .map(item -> new CartItemGetResponseDto(
                        item.getId(),
                        item.getProductId(),
                        item.getVariantId(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice(),
                        item.getAddedAt()
                ))
                .toList();

        return new CartGetResponseDto(
                cart.getId(),
                items,
                items.size(),
                cart.getTotalAmount()
        );
    }
}