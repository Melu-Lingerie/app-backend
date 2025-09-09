package ru.melulingerie.facade.cart.service;

import ru.melulingerie.facade.cart.dto.CartCreateFacadeResponseDto;

public interface CartCreateFacadeService {
    
    /**
     * Создает новую корзину для пользователя
     * 
     * @param userId ID пользователя для которого создается корзина
     * @return ответ с информацией о созданной корзине
     */
    CartCreateFacadeResponseDto createCart(Long userId);
}