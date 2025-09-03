package ru.melulingerie.service;

import ru.melulingerie.dto.CartGetResponseDto;

public interface CartGetService {

    CartGetResponseDto getCart(Long cartId);
}