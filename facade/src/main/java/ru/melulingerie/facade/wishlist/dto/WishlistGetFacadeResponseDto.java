package ru.melulingerie.facade.wishlist.dto;

import java.util.List;

public record WishlistGetFacadeResponseDto(List<WishlistItemGetFacadeResponseDto> items, Integer itemsCount) {}