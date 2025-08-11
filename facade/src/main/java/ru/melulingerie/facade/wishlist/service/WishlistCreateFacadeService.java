package ru.melulingerie.facade.wishlist.service;

/**
 * Фасадный сервис для создания wishlist
 */
public interface WishlistCreateFacadeService {

    /**
     * Создает wishlist для пользователя (идемпотентная операция)
     * @param userId идентификатор пользователя
     * @return идентификатор созданного или существующего wishlist
     */
    Long createWishlistForUser(Long userId);
}