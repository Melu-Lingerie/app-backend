package ru.melulingerie.cart.service;

import ru.melulingerie.cart.dto.response.CartCreateResponseDto;

public interface CartCreateService {
    
    /**
     * Создает новую корзину для пользователя
     * 
     * @param userId ID пользователя для которого создается корзина
     * @return ответ с информацией о созданной корзине
     */
    CartCreateResponseDto createCart(Long userId);
}