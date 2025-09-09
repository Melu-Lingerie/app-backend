package ru.melulingerie.cart.service;

import ru.melulingerie.cart.dto.request.CartAddItemRequestDto;
import ru.melulingerie.cart.dto.response.CartAddItemResponseDto;

public interface CartAddItemService {

    CartAddItemResponseDto addCartItem(Long cartId, CartAddItemRequestDto request);
}