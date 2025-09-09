package ru.melulingerie.cart.service;

public interface CartUpdateQuantityService {

    void updateItemQuantity(Long cartId, Long itemId, Integer quantity);
}