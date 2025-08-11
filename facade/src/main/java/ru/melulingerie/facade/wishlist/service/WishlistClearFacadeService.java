package ru.melulingerie.facade.wishlist.service;

/**
 * Фасадный сервис для очистки избранных товаров
 */
public interface WishlistClearFacadeService {

    /**
     * Очищает wishlist по wishlistId (идемпотентная операция)
     * @param wishlistId идентификатор избранных товаров
     * @return количество удаленных элементов
     */
    int clearWishlist(Long wishlistId);
}