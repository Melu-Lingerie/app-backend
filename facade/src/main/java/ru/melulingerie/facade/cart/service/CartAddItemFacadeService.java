package ru.melulingerie.facade.cart.service;

import ru.melulingerie.facade.cart.dto.CartAddFacadeRequestDto;
import ru.melulingerie.facade.cart.dto.CartAddFacadeResponseDto;

public interface CartAddItemFacadeService {

    CartAddFacadeResponseDto addItemToCart(Long cartId, CartAddFacadeRequestDto request);
}