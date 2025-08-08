package ru.mellingerie.facade.wishlist.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.mellingerie.facade.config.MapStructConfig;
import ru.mellingerie.facade.wishlist.dto.*;
import ru.mellingerie.wishlist.model.*;

@Mapper(
    config = MapStructConfig.class,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WishlistFacadeMapper {

    // Core -> Facade
    WishlistResponseDto toDto(WishlistModel model);
    WishlistItemDto toDto(WishlistItemModel model);
    AddToWishlistResponseDto toDto(AddToWishlistResponseModel model);

    // Facade -> Core
    AddToWishlistModel toCore(AddToWishlistRequestDto requestDto);
}


