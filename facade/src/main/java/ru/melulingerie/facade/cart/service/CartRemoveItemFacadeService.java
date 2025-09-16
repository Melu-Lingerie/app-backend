package ru.melulingerie.facade.cart.service;

import java.util.List;

public interface CartRemoveItemFacadeService {

    void removeItemsFromCart(Long cartId, List<Long> itemIds);

}
