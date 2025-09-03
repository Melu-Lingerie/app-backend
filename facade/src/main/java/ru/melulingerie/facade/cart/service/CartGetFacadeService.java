package ru.melulingerie.facade.cart.service;

import ru.melulingerie.facade.cart.dto.CartGetFacadeResponseDto;

public interface CartGetFacadeService {

    CartGetFacadeResponseDto getCart(Long cartId);
}