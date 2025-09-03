package ru.melulingerie.service;

import java.util.List;

public interface CartRemoveItemService {

    void removeCartItems(Long cartId, List<Long> itemIds);
}