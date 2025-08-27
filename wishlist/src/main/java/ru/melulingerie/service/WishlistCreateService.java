package ru.melulingerie.service;

/**
 * Доменный сервис для создания wishlist
 */
public interface WishlistCreateService {

    /**
     * Создает wishlist для пользователя (идемпотентная операция)
     * @param userId идентификатор пользователя
     * @return идентификатор созданного или существующего wishlist
     */
    Long createWishlistForUser(Long userId);
}