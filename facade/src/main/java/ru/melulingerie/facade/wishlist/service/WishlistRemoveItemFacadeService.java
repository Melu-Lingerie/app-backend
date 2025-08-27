package ru.melulingerie.facade.wishlist.service;

import java.util.List;

/**
 * Фасадный сервис для удаления элементов из избранных товаров
 */
public interface WishlistRemoveItemFacadeService {

    /**
     * Удаляет элементы из избранных товаров пользователя
     * @param wishlistId идентификатор избранных товаров
     * @param wishlistItemIds список идентификаторов элементов wishlist для удаления
     */
    void removeItemsFromWishlist(Long wishlistId, List<Long> wishlistItemIds);
}