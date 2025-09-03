package ru.melulingerie.service;

import ru.melulingerie.dto.CartUpdateQuantityRequestDto;

public interface CartUpdateQuantityService {

    void updateItemQuantity(Long cartId, Long itemId, CartUpdateQuantityRequestDto request);
}