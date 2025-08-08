package ru.mellingerie.wishlist.model;

import java.util.List;

public record WishlistModel(List<WishlistItemModel> items, Integer itemsCount) {}


