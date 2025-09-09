package ru.melulingerie.facade.cart.service;

import ru.melulingerie.facade.cart.dto.request.CartAddFacadeRequestDto;
import ru.melulingerie.facade.cart.dto.response.CartAddFacadeResponseDto;

public interface CartAddItemFacadeService {

    CartAddFacadeResponseDto addItemToCart(Long cartId, CartAddFacadeRequestDto request);
}
