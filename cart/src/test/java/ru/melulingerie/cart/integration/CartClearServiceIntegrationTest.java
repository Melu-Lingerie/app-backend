package ru.melulingerie.cart.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.melulingerie.cart.domain.Cart;
import ru.melulingerie.cart.domain.CartItem;
import ru.melulingerie.cart.service.CartClearService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CartClearService Integration Tests")
class CartClearServiceIntegrationTest extends CartIntegrationTestBase {

    @Autowired
    private CartClearService cartClearService;

    @AfterEach
    void cleanUp() {
        clearDatabase();
    }

    @Test
    @DisplayName("Should clear cart with multiple items successfully")
    void shouldClearCartWithMultipleItemsSuccessfully() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        CartItem item1 = createTestCartItem(cart, 100L, 200L, 2);
        CartItem item2 = createTestCartItem(cart, 101L, 201L, 3);
        CartItem item3 = createTestCartItem(cart, 102L, 202L, 1);

        // When
        Integer clearedCount = cartClearService.clearCart(cart.getId());

        // Then
        assertEquals(3, clearedCount);

        // Verify cart still exists but is not deleted
        Optional<Cart> cartAfterClear = cartRepository.findById(cart.getId());
        assertTrue(cartAfterClear.isPresent());
        assertFalse(cartAfterClear.get().isDeleted());

        // Verify all items are completely removed from database
        Optional<CartItem> deletedItem1 = cartItemRepository.findById(item1.getId());
        Optional<CartItem> deletedItem2 = cartItemRepository.findById(item2.getId());
        Optional<CartItem> deletedItem3 = cartItemRepository.findById(item3.getId());

        assertFalse(deletedItem1.isPresent(), "Item1 should be deleted from database");
        assertFalse(deletedItem2.isPresent(), "Item2 should be deleted from database");
        assertFalse(deletedItem3.isPresent(), "Item3 should be deleted from database");

        // Verify no items exist in database
        List<CartItem> allItems = cartItemRepository.findAll();
        assertEquals(0, allItems.size());
    }

    @Test
    @DisplayName("Should clear empty cart and return zero")
    void shouldClearEmptyCartAndReturnZero() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);

        // When
        Integer clearedCount = cartClearService.clearCart(cart.getId());

        // Then
        assertEquals(0, clearedCount);

        // Verify cart still exists
        Optional<Cart> cartAfterClear = cartRepository.findById(cart.getId());
        assertTrue(cartAfterClear.isPresent());
        assertFalse(cartAfterClear.get().isDeleted());
    }

    @Test
    @DisplayName("Should clear cart with single item")
    void shouldClearCartWithSingleItem() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        CartItem item = createTestCartItem(cart, 100L, 200L, 5);

        // When
        Integer clearedCount = cartClearService.clearCart(cart.getId());

        // Then
        assertEquals(1, clearedCount);

        // Verify cart still exists
        Optional<Cart> cartAfterClear = cartRepository.findById(cart.getId());
        assertTrue(cartAfterClear.isPresent());

        // Verify item is completely removed from database
        Optional<CartItem> deletedItem = cartItemRepository.findById(item.getId());
        assertFalse(deletedItem.isPresent(), "Item should be completely removed from database");
    }

    @Test
    @DisplayName("Should throw exception when cart not found")
    void shouldThrowExceptionWhenCartNotFound() {
        // Given
        Long nonExistentCartId = 999L;

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cartClearService.clearCart(nonExistentCartId)
        );

        assertEquals("Cart not found with id: " + nonExistentCartId, exception.getMessage());
    }

    @Test
    @DisplayName("Should handle clearing already empty cart")
    void shouldHandleClearingAlreadyEmptyCart() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        CartItem item1 = createTestCartItem(cart, 100L, 200L, 2);
        CartItem item2 = createTestCartItem(cart, 101L, 201L, 3);

        // First clear
        Integer firstClearCount = cartClearService.clearCart(cart.getId());
        assertEquals(2, firstClearCount);

        // When - clear again
        Integer secondClearCount = cartClearService.clearCart(cart.getId());

        // Then
        assertEquals(0, secondClearCount, "Second clear should return 0 as cart is already empty");

        // Verify all items are completely removed
        Optional<CartItem> deletedItem1 = cartItemRepository.findById(item1.getId());
        Optional<CartItem> deletedItem2 = cartItemRepository.findById(item2.getId());
        
        assertFalse(deletedItem1.isPresent(), "Item1 should be completely removed");
        assertFalse(deletedItem2.isPresent(), "Item2 should be completely removed");
        
        // Verify no items exist in database
        List<CartItem> allItems = cartItemRepository.findAll();
        assertEquals(0, allItems.size());
    }

    @Test
    @DisplayName("Should handle clearing large cart efficiently")
    void shouldHandleClearingLargeCartEfficiently() {
        // Given
        Long userId = 42L;
        Cart cart = createCartWithItems(userId, 100); // Create cart with 100 items

        // When
        long startTime = System.currentTimeMillis();
        Integer clearedCount = cartClearService.clearCart(cart.getId());
        long endTime = System.currentTimeMillis();

        // Then
        assertEquals(100, clearedCount);

        // Verify all items are completely removed from database
        List<CartItem> allItems = cartItemRepository.findAll();
        assertEquals(0, allItems.size(), "All items should be completely removed from database");

        // Verify cart still exists
        Optional<Cart> cartAfterClear = cartRepository.findById(cart.getId());
        assertTrue(cartAfterClear.isPresent());
        assertFalse(cartAfterClear.get().isDeleted());

        // Performance should be reasonable
        assertTrue(endTime - startTime < 1000, "Cart clearing should be fast even with many items");
    }

    @Test
    @DisplayName("Should preserve cart properties after clearing")
    void shouldPreserveCartPropertiesAfterClearing() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        createTestCartItem(cart, 100L, 200L, 2);
        createTestCartItem(cart, 101L, 201L, 3);

        // Store original cart properties
        Long originalCartId = cart.getId();
        Long originalUserId = cart.getUserId();

        // When
        cartClearService.clearCart(cart.getId());

        // Then
        Cart cartAfterClear = cartRepository.findById(cart.getId()).orElseThrow();

        assertEquals(originalCartId, cartAfterClear.getId());
        assertEquals(originalUserId, cartAfterClear.getUserId());
        assertFalse(cartAfterClear.isDeleted());
        assertNotNull(cartAfterClear.getCreatedAt());
        assertNotNull(cartAfterClear.getUpdatedAt());
    }

    @Test
    @DisplayName("Should not affect other carts when clearing specific cart")
    void shouldNotAffectOtherCartsWhenClearingSpecificCart() {
        // Given
        Long userId1 = 42L;
        Long userId2 = 43L;
        Cart cart1 = createTestCart(userId1);
        Cart cart2 = createTestCart(userId2);

        CartItem item1 = createTestCartItem(cart1, 100L, 200L, 2);
        CartItem item2 = createTestCartItem(cart2, 101L, 201L, 3);

        // When
        Integer clearedCount = cartClearService.clearCart(cart1.getId());

        // Then
        assertEquals(1, clearedCount);

        // Verify cart1's item is completely removed
        Optional<CartItem> deletedItem = cartItemRepository.findById(item1.getId());
        assertFalse(deletedItem.isPresent(), "Cart1's item should be completely removed");

        // Verify cart2's item is unaffected
        Optional<CartItem> unaffectedItem = cartItemRepository.findById(item2.getId());
        assertTrue(unaffectedItem.isPresent(), "Cart2's item should still exist");
        assertEquals(item2.getProductId(), unaffectedItem.get().getProductId());
        assertEquals(item2.getVariantId(), unaffectedItem.get().getVariantId());

        // Verify both carts still exist
        assertTrue(cartRepository.findById(cart1.getId()).isPresent());
        assertTrue(cartRepository.findById(cart2.getId()).isPresent());
    }

    @Test
    @DisplayName("Should return correct count when clearing cart multiple times")
    void shouldReturnCorrectCountWhenClearingCartMultipleTimes() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        createTestCartItem(cart, 100L, 200L, 2);
        createTestCartItem(cart, 101L, 201L, 3);

        // When - First clear
        Integer firstClearCount = cartClearService.clearCart(cart.getId());

        // When - Second clear
        Integer secondClearCount = cartClearService.clearCart(cart.getId());

        // Then
        assertEquals(2, firstClearCount);
        assertEquals(0, secondClearCount); // No active items to clear

        // Verify all items are completely removed from database
        List<CartItem> allItems = cartItemRepository.findAll();
        assertEquals(0, allItems.size(), "All items should be completely removed from database");
    }
}