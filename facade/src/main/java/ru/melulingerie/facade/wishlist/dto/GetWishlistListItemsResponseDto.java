package ru.melulingerie.facade.wishlist.dto;

import ru.melulingerie.dto.GetWishlistItemResponseDto;

import java.util.List;

public record GetWishlistListItemsResponseDto(List<GetWishlistItemResponseDto> items, Integer itemsCount) {}