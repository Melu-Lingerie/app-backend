package ru.melulingerie.service;

import ru.melulingerie.dto.CartAddItemRequestDto;
import ru.melulingerie.dto.CartAddItemResponseDto;

public interface CartAddItemService {

    CartAddItemResponseDto addCartItem(Long cartId, CartAddItemRequestDto request);
}