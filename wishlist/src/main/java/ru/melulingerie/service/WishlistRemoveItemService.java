package ru.melulingerie.service;

import java.util.List;

/**
 * Доменный сервис для удаления элементов из wishlist
 */
public interface WishlistRemoveItemService {

    /**
     * Удаляет элементы из wishlist пользователя
     * @param wishlistId идентификатор избранных товаров
     * @param wishlistItemIds список идентификаторов элементов wishlist для удаления
     */
    void removeWishlistItems(Long wishlistId, List<Long> wishlistItemIds);
}