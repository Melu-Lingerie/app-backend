package ru.melulingerie.cart.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.melulingerie.cart.domain.Cart;
import ru.melulingerie.cart.domain.CartItem;
import ru.melulingerie.cart.service.CartRemoveItemService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CartRemoveItemService Integration Tests")
class CartRemoveItemServiceIntegrationTest extends CartIntegrationTestBase {

    @Autowired
    private CartRemoveItemService cartRemoveItemService;

    @AfterEach
    void cleanUp() {
        clearDatabase();
    }

    @Test
    @DisplayName("Should remove single item from cart successfully")
    void shouldRemoveSingleItemFromCartSuccessfully() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        CartItem item1 = createTestCartItem(cart, 100L, 200L, 2);
        CartItem item2 = createTestCartItem(cart, 101L, 201L, 3);

        List<Long> itemIdsToRemove = Collections.singletonList(item1.getId());

        // When
        cartRemoveItemService.removeCartItems(cart.getId(), itemIdsToRemove);

        // Then
        Optional<CartItem> removedItem = cartItemRepository.findById(item1.getId());
        assertFalse(removedItem.isPresent(), "Removed item should be completely deleted from database");

        Optional<CartItem> remainingItem = cartItemRepository.findById(item2.getId());
        assertTrue(remainingItem.isPresent(), "Remaining item should still exist");
        assertEquals(item2.getProductId(), remainingItem.get().getProductId());
        assertEquals(item2.getVariantId(), remainingItem.get().getVariantId());
    }

    @Test
    @DisplayName("Should remove multiple items from cart successfully")
    void shouldRemoveMultipleItemsFromCartSuccessfully() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        CartItem item1 = createTestCartItem(cart, 100L, 200L, 2);
        CartItem item2 = createTestCartItem(cart, 101L, 201L, 3);
        CartItem item3 = createTestCartItem(cart, 102L, 202L, 1);

        List<Long> itemIdsToRemove = Arrays.asList(item1.getId(), item3.getId());

        // When
        cartRemoveItemService.removeCartItems(cart.getId(), itemIdsToRemove);

        // Then
        Optional<CartItem> removedItem1 = cartItemRepository.findById(item1.getId());
        Optional<CartItem> removedItem3 = cartItemRepository.findById(item3.getId());
        Optional<CartItem> remainingItem2 = cartItemRepository.findById(item2.getId());

        assertFalse(removedItem1.isPresent(), "Item1 should be completely removed");
        assertFalse(removedItem3.isPresent(), "Item3 should be completely removed");
        assertTrue(remainingItem2.isPresent(), "Item2 should remain");
        assertEquals(item2.getProductId(), remainingItem2.get().getProductId());
    }

    @Test
    @DisplayName("Should remove all items from cart")
    void shouldRemoveAllItemsFromCart() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        CartItem item1 = createTestCartItem(cart, 100L, 200L, 2);
        CartItem item2 = createTestCartItem(cart, 101L, 201L, 3);
        CartItem item3 = createTestCartItem(cart, 102L, 202L, 1);

        List<Long> itemIdsToRemove = Arrays.asList(item1.getId(), item2.getId(), item3.getId());

        // When
        cartRemoveItemService.removeCartItems(cart.getId(), itemIdsToRemove);

        // Then
        List<CartItem> allItems = cartItemRepository.findAll();
        assertEquals(0, allItems.size(), "All items should be completely removed from database");
    }

    @Test
    @DisplayName("Should throw exception when cart not found")
    void shouldThrowExceptionWhenCartNotFound() {
        // Given
        Long nonExistentCartId = 999L;
        List<Long> itemIds = Arrays.asList(1L, 2L);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cartRemoveItemService.removeCartItems(nonExistentCartId, itemIds)
        );

        assertEquals("Cart not found with id: " + nonExistentCartId, exception.getMessage());
    }

    @Test
    @DisplayName("Should handle removing non-existent items gracefully")
    void shouldHandleRemovingNonExistentItemsGracefully() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        CartItem existingItem = createTestCartItem(cart, 100L, 200L, 2);

        List<Long> itemIdsToRemove = Arrays.asList(existingItem.getId(), 999L, 888L);

        // When - Should not throw exception
        assertDoesNotThrow(() ->
                cartRemoveItemService.removeCartItems(cart.getId(), itemIdsToRemove)
        );

        // Then - Existing item should be removed, non-existent IDs are ignored
        Optional<CartItem> deletedItem = cartItemRepository.findById(existingItem.getId());
        assertFalse(deletedItem.isPresent(), "Existing item should be completely removed from database");
    }

    @Test
    @DisplayName("Should handle duplicate item IDs in remove request")
    void shouldHandleDuplicateItemIdsInRemoveRequest() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        CartItem item1 = createTestCartItem(cart, 100L, 200L, 2);
        CartItem item2 = createTestCartItem(cart, 101L, 201L, 3);

        // Include duplicates in the list
        List<Long> itemIdsToRemove = Arrays.asList(item1.getId(), item2.getId(), item1.getId(), item2.getId());

        // When - Should not throw exception and handle duplicates gracefully
        assertDoesNotThrow(() ->
                cartRemoveItemService.removeCartItems(cart.getId(), itemIdsToRemove)
        );

        // Then - Both items should be removed once
        Optional<CartItem> deletedItem1 = cartItemRepository.findById(item1.getId());
        Optional<CartItem> deletedItem2 = cartItemRepository.findById(item2.getId());

        assertFalse(deletedItem1.isPresent(), "Item1 should be completely removed");
        assertFalse(deletedItem2.isPresent(), "Item2 should be completely removed");
        
        // Verify no items remain in database
        List<CartItem> allItems = cartItemRepository.findAll();
        assertEquals(0, allItems.size());
    }

    @Test
    @DisplayName("Should preserve cart when removing all items")
    void shouldPreserveCartWhenRemovingAllItems() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        CartItem item1 = createTestCartItem(cart, 100L, 200L, 2);
        CartItem item2 = createTestCartItem(cart, 101L, 201L, 3);

        List<Long> itemIdsToRemove = Arrays.asList(item1.getId(), item2.getId());

        // When
        cartRemoveItemService.removeCartItems(cart.getId(), itemIdsToRemove);

        // Then
        Optional<Cart> cartAfterRemoval = cartRepository.findById(cart.getId());
        assertTrue(cartAfterRemoval.isPresent());
        assertFalse(cartAfterRemoval.get().isDeleted());
        assertEquals(userId, cartAfterRemoval.get().getUserId());
    }

    @Test
    @DisplayName("Should handle removing items from different carts")
    void shouldHandleRemovingItemsFromDifferentCarts() {
        // Given
        Long userId1 = 42L;
        Long userId2 = 43L;
        Cart cart1 = createTestCart(userId1);
        Cart cart2 = createTestCart(userId2);

        CartItem item1 = createTestCartItem(cart1, 100L, 200L, 2);
        CartItem item2 = createTestCartItem(cart2, 101L, 201L, 3);

        // Try to remove item from cart2 using cart1's ID
        List<Long> itemIdsToRemove = Collections.singletonList(item2.getId());

        // When
        cartRemoveItemService.removeCartItems(cart1.getId(), itemIdsToRemove);

        // Then - both items should not be affected since they belong to cart2, not cart1
        Optional<CartItem> unaffectedItem2 = cartItemRepository.findById(item2.getId());
        assertTrue(unaffectedItem2.isPresent(), "Cart2's item should remain unaffected");
        
        Optional<CartItem> unaffectedItem1 = cartItemRepository.findById(item1.getId());
        assertTrue(unaffectedItem1.isPresent(), "Cart2's item should remain unaffected");
    }

    @Test
    @DisplayName("Should handle large batch removal efficiently")
    void shouldHandleLargeBatchRemovalEfficiently() {
        // Given
        Long userId = 42L;
        Cart cart = createCartWithItems(userId, 50); // Create cart with 50 items

        // Get all item IDs
        List<CartItem> allItems = cartItemRepository.findAll();
        assertEquals(50, allItems.size());

        List<Long> itemIdsToRemove = allItems.stream()
                .map(CartItem::getId)
                .toList();

        // When
        long startTime = System.currentTimeMillis();
        cartRemoveItemService.removeCartItems(cart.getId(), itemIdsToRemove);
        long endTime = System.currentTimeMillis();

        // Then
        List<CartItem> itemsAfterRemoval = cartItemRepository.findAll();
        assertEquals(0, itemsAfterRemoval.size(), "All removed items should be completely deleted from database");

        // Performance should be reasonable
        assertTrue(endTime - startTime < 1000, "Batch removal should be fast");
    }

    @Test
    @DisplayName("Should handle empty item list gracefully")
    void shouldHandleEmptyItemListGracefully() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        CartItem item = createTestCartItem(cart, 100L, 200L, 2);

        List<Long> emptyItemIds = Collections.emptyList();

        // When & Then
        assertDoesNotThrow(() ->
                cartRemoveItemService.removeCartItems(cart.getId(), emptyItemIds)
        );

        // Verify no items were affected
        List<CartItem> allItems = cartItemRepository.findAll();
        assertEquals(1, allItems.size(), "Only one item should remain");
        assertEquals(item.getProductId(), allItems.get(0).getProductId());
    }
}