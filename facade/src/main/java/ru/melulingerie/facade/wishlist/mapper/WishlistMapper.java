package ru.melulingerie.facade.wishlist.mapper;

import org.mapstruct.Mapper;
import ru.melulingerie.dto.AddItemToWishlistRequestDto;
import ru.melulingerie.dto.AddItemToWishlistResponseDto;
import ru.melulingerie.dto.GetWishlistResponseDto;
import ru.melulingerie.facade.wishlist.dto.GetWishlistListItemsResponseDto;
import ru.melulingerie.facade.wishlist.dto.AddWishlistRequestDto;
import ru.melulingerie.facade.wishlist.dto.AddWishlistResponseDto;

@Mapper(componentModel = "spring")
public interface WishlistMapper {

    /**
     * Маппинг запроса от фасада к модулю
     */
    AddItemToWishlistRequestDto toModuleRequestDto(AddWishlistRequestDto facadeRequest);

    /**
     * Маппинг ответа от модуля к фасаду
     */
    AddWishlistResponseDto toFacadeResponseDto(AddItemToWishlistResponseDto moduleResponse);

    /**
     * Маппинг списка элементов wishlist от модуля к фасаду
     */
    GetWishlistListItemsResponseDto toFacadeWishListResponseDto(GetWishlistResponseDto moduleResponse);
}

