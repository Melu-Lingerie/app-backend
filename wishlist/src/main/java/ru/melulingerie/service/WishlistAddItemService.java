package ru.melulingerie.service;

import ru.melulingerie.dto.WishlistAddItemRequestDto;
import ru.melulingerie.dto.WishlistAddItemResponseDto;

/**
 * Доменный сервис для добавления элементов в wishlist
 */
public interface WishlistAddItemService {

    /**
     * Добавляет элемент в wishlist по wishlistId
     * @param wishlistId идентификатор wishlist
     * @param request запрос на добавление элемента
     * @return ответ с информацией о добавленном элементе
     */
    WishlistAddItemResponseDto addWishlistItem(Long wishlistId, WishlistAddItemRequestDto request);
}