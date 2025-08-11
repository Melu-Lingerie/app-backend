package ru.melulingerie.facade.wishlist.mapper;

import org.mapstruct.Mapper;
import ru.melulingerie.dto.AddItemToWishlistRequestDto;
import ru.melulingerie.dto.AddItemToWishlistResponseDto;
import ru.melulingerie.dto.WishlistResponseDto;
import ru.melulingerie.facade.wishlist.dto.WishlistApiListItemsResponseDto;
import ru.melulingerie.facade.wishlist.dto.WishlistApiRequestDto;
import ru.melulingerie.facade.wishlist.dto.WishlistApiResponseDto;

@Mapper(componentModel = "spring")
public interface WishlistMapper {

    /**
     * Маппинг запроса от фасада к модулю
     */
    AddItemToWishlistRequestDto toModuleRequestDto(WishlistApiRequestDto facadeRequest);

    /**
     * Маппинг ответа от модуля к фасаду
     */
    WishlistApiResponseDto toFacadeResponseDto(AddItemToWishlistResponseDto moduleResponse);

    /**
     * Маппинг списка элементов wishlist от модуля к фасаду
     */
    WishlistApiListItemsResponseDto toFacadeWishListResponseDto(WishlistResponseDto moduleResponse);
}

