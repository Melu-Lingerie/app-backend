package ru.melulingerie.facade.wishlist.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.melulingerie.dto.WishlistAddItemRequestDto;
import ru.melulingerie.dto.WishlistAddItemResponseDto;
import ru.melulingerie.dto.WishlistGetResponseDto;
import ru.melulingerie.dto.WishlistItemGetResponseDto;
import ru.melulingerie.facade.wishlist.dto.WishlistAddFacadeRequestDto;
import ru.melulingerie.facade.wishlist.dto.WishlistAddFacadeResponseDto;
import ru.melulingerie.facade.wishlist.dto.WishlistGetFacadeResponseDto;
import ru.melulingerie.facade.wishlist.dto.WishlistItemGetFacadeResponseDto;

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
    @Mapping(target = "items", source = "items")
    WishlistGetFacadeResponseDto toFacadeWishListResponseDto(WishlistGetResponseDto domainResponse);

    /**
     * Маппинг элемента wishlist от модуля к фасаду
     */
    WishlistItemGetFacadeResponseDto toFacadeWishlistItemDto(WishlistItemGetResponseDto domainItem);
}