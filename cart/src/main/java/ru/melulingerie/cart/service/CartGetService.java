package ru.melulingerie.cart.service;

import ru.melulingerie.cart.dto.response.CartGetResponseDto;

public interface CartGetService {

    CartGetResponseDto getCart(Long cartId);
}