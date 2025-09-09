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
        assertTrue(removedItem.isPresent());
        assertTrue(removedItem.get().isDeleted()); // Should be soft deleted

        Optional<CartItem> remainingItem = cartItemRepository.findById(item2.getId());
        assertTrue(remainingItem.isPresent());
        assertFalse(remainingItem.get().isDeleted()); // Should remain active
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
        CartItem removedItem1 = cartItemRepository.findById(item1.getId()).orElseThrow();
        CartItem removedItem3 = cartItemRepository.findById(item3.getId()).orElseThrow();
        CartItem remainingItem2 = cartItemRepository.findById(item2.getId()).orElseThrow();

        assertTrue(removedItem1.isDeleted());
        assertTrue(removedItem3.isDeleted());
        assertFalse(remainingItem2.isDeleted());
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
        assertEquals(3, allItems.size()); // Items still exist in DB

        // But all should be marked as deleted
        allItems.forEach(item -> assertTrue(item.isDeleted()));
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

        // Then - Existing item should be deleted, non-existent IDs are ignored
        CartItem deletedItem = cartItemRepository.findById(existingItem.getId()).orElseThrow();
        assertTrue(deletedItem.isDeleted());
    }

    @Test
    @DisplayName("Should handle removing already deleted items")
    void shouldHandleRemovingAlreadyDeletedItems() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        CartItem item1 = createTestCartItem(cart, 100L, 200L, 2);
        CartItem item2 = createTestCartItem(cart, 101L, 201L, 3);

        // Pre-delete item1
        item1.markAsDeleted();
        cartItemRepository.save(item1);

        List<Long> itemIdsToRemove = Arrays.asList(item1.getId(), item2.getId());

        // When
        cartRemoveItemService.removeCartItems(cart.getId(), itemIdsToRemove);

        // Then
        CartItem stillDeletedItem1 = cartItemRepository.findById(item1.getId()).orElseThrow();
        CartItem newlyDeletedItem2 = cartItemRepository.findById(item2.getId()).orElseThrow();

        assertTrue(stillDeletedItem1.isDeleted());
        assertTrue(newlyDeletedItem2.isDeleted());
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

        // Then - item2 should not be affected since it belongs to cart2
        CartItem unaffectedItem = cartItemRepository.findById(item2.getId()).orElseThrow();
        assertFalse(unaffectedItem.isDeleted());

        CartItem unaffectedItem1 = cartItemRepository.findById(item1.getId()).orElseThrow();
        assertFalse(unaffectedItem1.isDeleted());
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
        assertEquals(50, itemsAfterRemoval.size()); // Items still exist

        // All should be marked as deleted
        itemsAfterRemoval.forEach(item -> assertTrue(item.isDeleted()));

        // Performance should be reasonable
        assertTrue(endTime - startTime < 1000, "Batch removal should be fast");
    }

    @Test
    @DisplayName("Should handle empty item list gracefully")
    void shouldHandleEmptyItemListGracefully() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        createTestCartItem(cart, 100L, 200L, 2);

        List<Long> emptyItemIds = Collections.emptyList();

        // When & Then
        assertDoesNotThrow(() ->
                cartRemoveItemService.removeCartItems(cart.getId(), emptyItemIds)
        );

        // Verify no items were affected
        List<CartItem> allItems = cartItemRepository.findAll();
        assertEquals(1, allItems.size());
        assertFalse(allItems.get(0).isDeleted());
    }
}