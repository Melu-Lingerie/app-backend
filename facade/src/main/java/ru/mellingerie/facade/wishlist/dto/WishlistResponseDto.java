package ru.mellingerie.facade.wishlist.dto;

import java.util.List;

public record WishlistResponseDto(List<WishlistItemDto> items, Integer itemsCount) {}


