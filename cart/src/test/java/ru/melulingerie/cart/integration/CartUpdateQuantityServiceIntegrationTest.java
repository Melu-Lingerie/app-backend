package ru.melulingerie.cart.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.melulingerie.cart.domain.Cart;
import ru.melulingerie.cart.domain.CartItem;
import ru.melulingerie.cart.service.CartUpdateQuantityService;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CartUpdateQuantityService Integration Tests")
class CartUpdateQuantityServiceIntegrationTest extends CartIntegrationTestBase {

    @Autowired
    private CartUpdateQuantityService cartUpdateQuantityService;

    @AfterEach
    void cleanUp() {
        clearDatabase();
    }

    @Test
    @DisplayName("Should update item quantity successfully")
    void shouldUpdateItemQuantitySuccessfully() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        CartItem item = createTestCartItem(cart, 100L, 200L, 3);
        
        // When
        cartUpdateQuantityService.updateItemQuantity(cart.getId(), item.getId(), 7);

        // Then
        CartItem updatedItem = cartItemRepository.findById(item.getId()).orElseThrow();
        assertEquals(7, updatedItem.getQuantity());
        // Item exists and is updated successfully
    }

    @Test
    @DisplayName("Should update quantity from 1 to maximum allowed")
    void shouldUpdateQuantityFromOneToMaximumAllowed() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        CartItem item = createTestCartItem(cart, 100L, 200L, 1);
        
        // When
        cartUpdateQuantityService.updateItemQuantity(cart.getId(), item.getId(), 99); // Maximum allowed

        // Then
        CartItem updatedItem = cartItemRepository.findById(item.getId()).orElseThrow();
        assertEquals(99, updatedItem.getQuantity());
    }

    @Test
    @DisplayName("Should update quantity from high to low value")
    void shouldUpdateQuantityFromHighToLowValue() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        CartItem item = createTestCartItem(cart, 100L, 200L, 50);
        
        // When
        cartUpdateQuantityService.updateItemQuantity(cart.getId(), item.getId(), 2);

        // Then
        CartItem updatedItem = cartItemRepository.findById(item.getId()).orElseThrow();
        assertEquals(2, updatedItem.getQuantity());
    }

    @Test
    @DisplayName("Should handle updating quantity to same value")
    void shouldHandleUpdatingQuantityToSameValue() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        CartItem item = createTestCartItem(cart, 100L, 200L, 5);
        
        // When
        cartUpdateQuantityService.updateItemQuantity(cart.getId(), item.getId(), 5);

        // Then
        CartItem updatedItem = cartItemRepository.findById(item.getId()).orElseThrow();
        assertEquals(5, updatedItem.getQuantity());
        // Item exists and is updated successfully
    }

    @Test
    @DisplayName("Should throw exception when cart item not found")
    void shouldThrowExceptionWhenCartItemNotFound() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        Long nonExistentItemId = 999L;
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cartUpdateQuantityService.updateItemQuantity(cart.getId(), nonExistentItemId, 5)
        );
        
        assertEquals("Cart item not found. CartId: " + cart.getId() + ", ItemId: " + nonExistentItemId, 
                     exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when updating with invalid quantity")
    void shouldThrowExceptionWhenUpdatingWithInvalidQuantity() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        CartItem item = createTestCartItem(cart, 100L, 200L, 3);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cartUpdateQuantityService.updateItemQuantity(cart.getId(), item.getId(), 0)
        );
        
        assertEquals("Quantity must be positive: 0", exception.getMessage());
        
        // Verify original quantity remains unchanged
        CartItem unchangedItem = cartItemRepository.findById(item.getId()).orElseThrow();
        assertEquals(3, unchangedItem.getQuantity());
    }

    @Test
    @DisplayName("Should throw exception when updating with negative quantity")
    void shouldThrowExceptionWhenUpdatingWithNegativeQuantity() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        CartItem item = createTestCartItem(cart, 100L, 200L, 3);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cartUpdateQuantityService.updateItemQuantity(cart.getId(), item.getId(), -1)
        );
        
        assertEquals("Quantity must be positive: -1", exception.getMessage());
        
        // Verify original quantity remains unchanged
        CartItem unchangedItem = cartItemRepository.findById(item.getId()).orElseThrow();
        assertEquals(3, unchangedItem.getQuantity());
    }

    @Test
    @DisplayName("Should not update non-existent cart items")
    void shouldNotUpdateNonExistentCartItems() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        Long nonExistentItemId = 999L;
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cartUpdateQuantityService.updateItemQuantity(cart.getId(), nonExistentItemId, 10)
        );
        
        assertTrue(exception.getMessage().contains("Cart item not found"));
    }

    @Test
    @DisplayName("Should handle updating multiple different items")
    void shouldHandleUpdatingMultipleDifferentItems() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        CartItem item1 = createTestCartItem(cart, 100L, 200L, 2);
        CartItem item2 = createTestCartItem(cart, 101L, 201L, 5);
        CartItem item3 = createTestCartItem(cart, 102L, 202L, 1);
        
        // When
        cartUpdateQuantityService.updateItemQuantity(cart.getId(), item1.getId(), 8);
        cartUpdateQuantityService.updateItemQuantity(cart.getId(), item2.getId(), 3);
        cartUpdateQuantityService.updateItemQuantity(cart.getId(), item3.getId(), 15);

        // Then
        CartItem updatedItem1 = cartItemRepository.findById(item1.getId()).orElseThrow();
        CartItem updatedItem2 = cartItemRepository.findById(item2.getId()).orElseThrow();
        CartItem updatedItem3 = cartItemRepository.findById(item3.getId()).orElseThrow();
        
        assertEquals(8, updatedItem1.getQuantity());
        assertEquals(3, updatedItem2.getQuantity());
        assertEquals(15, updatedItem3.getQuantity());
    }

    @Test
    @DisplayName("Should preserve other item properties when updating quantity")
    void shouldPreserveOtherItemPropertiesWhenUpdatingQuantity() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        CartItem item = createTestCartItem(cart, 100L, 200L, 5);
        
        // Store original values
        Long originalProductId = item.getProductId();
        Long originalVariantId = item.getVariantId();
        Long originalCartId = item.getCart().getId();
        
        // When
        cartUpdateQuantityService.updateItemQuantity(cart.getId(), item.getId(), 12);

        // Then
        CartItem updatedItem = cartItemRepository.findById(item.getId()).orElseThrow();
        
        assertEquals(12, updatedItem.getQuantity()); // Quantity should be updated
        assertEquals(originalProductId, updatedItem.getProductId()); // Other properties preserved
        assertEquals(originalVariantId, updatedItem.getVariantId());
        assertEquals(originalCartId, updatedItem.getCart().getId());
        // Item exists and is updated successfully
    }
}