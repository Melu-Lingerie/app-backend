package ru.melulingerie.facade.cart.service;

public interface CartUpdateQuantityFacadeService {

    void updateItemQuantity(Long cartId, Long itemId, Integer quantity);
}
