package ru.melulingerie.cart.service;

import ru.melulingerie.cart.dto.request.CartUpdateQuantityRequestDto;

public interface CartUpdateQuantityService {

    void updateItemQuantity(Long cartId, Long itemId, CartUpdateQuantityRequestDto request);
}