package ru.melulingerie.facade.cart.service;

import ru.melulingerie.facade.cart.dto.CartUpdateQuantityFacadeRequestDto;

public interface CartUpdateQuantityFacadeService {

    void updateItemQuantity(Long cartId, Long itemId, CartUpdateQuantityFacadeRequestDto request);
}