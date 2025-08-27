package ru.melulingerie.service;

/**
 * Доменный сервис для очистки wishlist
 */
public interface WishlistClearService {

    /**
     * Очищает wishlist по wishlistId (идемпотентная операция)
     * @param wishlistId идентификатор wishlist
     * @return количество удаленных элементов
     */
    int clearWishlist(Long wishlistId);
}