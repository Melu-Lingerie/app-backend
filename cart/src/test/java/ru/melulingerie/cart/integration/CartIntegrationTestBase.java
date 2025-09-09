package ru.melulingerie.cart.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.cart.domain.Cart;
import ru.melulingerie.cart.domain.CartItem;
import ru.melulingerie.cart.repository.CartItemRepository;
import ru.melulingerie.cart.repository.CartRepository;

import java.util.ArrayList;

@SpringBootTest(classes = CartTestApplication.class)
@ActiveProfiles("test")
public abstract class CartIntegrationTestBase {

    @Autowired
    protected CartRepository cartRepository;

    @Autowired
    protected CartItemRepository cartItemRepository;

    protected Cart createTestCart(Long userId) {
        Cart cart = new Cart(userId);
        return cartRepository.save(cart);
    }

    protected CartItem createTestCartItem(Cart cart, Long productId, Long variantId, Integer quantity) {
        CartItem item = new CartItem(cart, productId, variantId, quantity);
        return cartItemRepository.save(item);
    }

    protected Cart createCartWithItems(Long userId, int itemCount) {
        Cart cart = createTestCart(userId);
        cart.setCartItems(new ArrayList<>());

        for (int i = 1; i <= itemCount; i++) {
            CartItem item = createTestCartItem(cart, (long) i, (long) (i + 100), i);
            cart.getCartItems().add(item);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    protected void clearDatabase() {
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
    }
}