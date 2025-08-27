package ru.melulingerie.dto;

import java.util.List;

public record WishlistGetResponseDto(Long wishlistId, List<WishlistItemGetResponseDto> items, Integer itemsCount) {}