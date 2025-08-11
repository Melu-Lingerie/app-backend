package ru.melulingerie.dto;

import java.util.List;

public record WishlistResponseDto(List<WishListItemResponseDto> items, Integer itemsCount) {}

