package ru.melulingerie.facade.wishlist.mapper;

import org.mapstruct.Mapper;
import ru.melulingerie.dto.WishlistAddItemRequestDto;
import ru.melulingerie.dto.WishlistAddItemResponseDto;
import ru.melulingerie.dto.WishlistGetResponseDto;
import ru.melulingerie.facade.wishlist.dto.WishlistAddFacadeRequestDto;
import ru.melulingerie.facade.wishlist.dto.WishlistAddFacadeResponseDto;
import ru.melulingerie.facade.wishlist.dto.WishlistGetFacadeResponseDto;

@Mapper(componentModel = "spring")
public interface WishlistMapper {

    /**
     * Маппинг запроса от фасада к модулю
     */
    WishlistAddItemRequestDto toModuleRequestDto(WishlistAddFacadeRequestDto facadeRequest);

    /**
     * Маппинг ответа от модуля к фасаду
     */
    WishlistAddFacadeResponseDto toFacadeResponseDto(WishlistAddItemResponseDto domainResponse);

    /**
     * Маппинг списка элементов wishlist от модуля к фасаду
     */
    WishlistGetFacadeResponseDto toFacadeWishListResponseDto(WishlistGetResponseDto domainResponse);
}