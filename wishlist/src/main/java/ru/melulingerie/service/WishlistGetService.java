package ru.melulingerie.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.melulingerie.dto.WishlistGetResponseDto;

/**
 * Доменный сервис для получения wishlist
 */
public interface WishlistGetService {

    /**
     * Получает wishlist по wishlistId с пагинацией
     *
     * @param wishlistId идентификатор wishlist
     * @return данные wishlist с пагинированными элементами
     */
    WishlistGetResponseDto getWishlist(Long wishlistId);
}