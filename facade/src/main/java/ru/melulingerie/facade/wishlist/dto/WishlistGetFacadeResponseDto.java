package ru.melulingerie.facade.wishlist.dto;

import ru.melulingerie.dto.WishlistItemGetResponseDto;

import java.util.List;

public record WishlistGetFacadeResponseDto(List<WishlistItemGetResponseDto> items, Integer itemsCount) {}