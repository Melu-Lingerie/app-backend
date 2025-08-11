package ru.melulingerie.facade.wishlist.dto;

import ru.melulingerie.dto.WishListItemResponseDto;

import java.util.List;

public record WishlistApiListItemsResponseDto (List<WishListItemResponseDto> items, Integer itemsCount) {}
