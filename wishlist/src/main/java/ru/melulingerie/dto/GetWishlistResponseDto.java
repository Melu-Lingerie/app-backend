package ru.melulingerie.dto;

import java.util.List;

public record GetWishlistResponseDto(List<GetWishlistItemResponseDto> items, Integer itemsCount) {}

