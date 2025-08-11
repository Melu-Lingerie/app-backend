package ru.melulingerie.facade.wishlist.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.melulingerie.dto.WishlistItemGetResponseDto;
import ru.melulingerie.facade.wishlist.dto.WishlistGetFacadeResponseDto;

/**
 * Фасадный сервис для получения избранных товаров
 */
public interface WishlistGetFacadeService {

    /**
     * Получает избранные товары по wishlistId с пагинацией
     * @param wishlistId идентификатор избранных товаров
     * @return данные wishlist
     */
    WishlistGetFacadeResponseDto getWishlist(Long wishlistId);
}