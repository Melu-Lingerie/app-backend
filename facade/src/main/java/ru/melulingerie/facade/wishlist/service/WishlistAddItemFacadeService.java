package ru.melulingerie.facade.wishlist.service;

import ru.melulingerie.facade.wishlist.dto.WishlistAddFacadeRequestDto;
import ru.melulingerie.facade.wishlist.dto.WishlistAddFacadeResponseDto;

/**
 * Фасадный сервис для добавления элементов в избранные товары
 */
public interface WishlistAddItemFacadeService {

    /**
     * Добавляет элемент в избранные товары по wishlistId
     * @param wishlistId идентификатор избранных товаров
     * @param request запрос на добавление
     * @return результат добавления
     */
    WishlistAddFacadeResponseDto addItemToWishlist(Long wishlistId, WishlistAddFacadeRequestDto request);
}