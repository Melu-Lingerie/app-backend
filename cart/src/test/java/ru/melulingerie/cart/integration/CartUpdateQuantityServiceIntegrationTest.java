package ru.melulingerie.cart.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.melulingerie.cart.domain.Cart;
import ru.melulingerie.cart.domain.CartItem;
import ru.melulingerie.cart.dto.request.CartUpdateQuantityRequestDto;
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
        
        CartUpdateQuantityRequestDto request = new CartUpdateQuantityRequestDto(7);

        // When
        cartUpdateQuantityService.updateItemQuantity(cart.getId(), item.getId(), request);

        // Then
        CartItem updatedItem = cartItemRepository.findById(item.getId()).orElseThrow();
        assertEquals(7, updatedItem.getQuantity());
        assertFalse(updatedItem.isDeleted());
    }

    @Test
    @DisplayName("Should update quantity from 1 to maximum allowed")
    void shouldUpdateQuantityFromOneToMaximumAllowed() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        CartItem item = createTestCartItem(cart, 100L, 200L, 1);
        
        CartUpdateQuantityRequestDto request = new CartUpdateQuantityRequestDto(99); // Maximum allowed

        // When
        cartUpdateQuantityService.updateItemQuantity(cart.getId(), item.getId(), request);

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
        
        CartUpdateQuantityRequestDto request = new CartUpdateQuantityRequestDto(2);

        // When
        cartUpdateQuantityService.updateItemQuantity(cart.getId(), item.getId(), request);

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
        
        CartUpdateQuantityRequestDto request = new CartUpdateQuantityRequestDto(5);

        // When
        cartUpdateQuantityService.updateItemQuantity(cart.getId(), item.getId(), request);

        // Then
        CartItem updatedItem = cartItemRepository.findById(item.getId()).orElseThrow();
        assertEquals(5, updatedItem.getQuantity());
        assertFalse(updatedItem.isDeleted());
    }

    @Test
    @DisplayName("Should throw exception when cart item not found")
    void shouldThrowExceptionWhenCartItemNotFound() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        Long nonExistentItemId = 999L;
        
        CartUpdateQuantityRequestDto request = new CartUpdateQuantityRequestDto(5);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cartUpdateQuantityService.updateItemQuantity(cart.getId(), nonExistentItemId, request)
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
        
        CartUpdateQuantityRequestDto request = new CartUpdateQuantityRequestDto(0);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cartUpdateQuantityService.updateItemQuantity(cart.getId(), item.getId(), request)
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
        
        CartUpdateQuantityRequestDto request = new CartUpdateQuantityRequestDto(-1);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cartUpdateQuantityService.updateItemQuantity(cart.getId(), item.getId(), request)
        );
        
        assertEquals("Quantity must be positive: -1", exception.getMessage());
        
        // Verify original quantity remains unchanged
        CartItem unchangedItem = cartItemRepository.findById(item.getId()).orElseThrow();
        assertEquals(3, unchangedItem.getQuantity());
    }

    @Test
    @DisplayName("Should not update deleted cart items")
    void shouldNotUpdateDeletedCartItems() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        CartItem item = createTestCartItem(cart, 100L, 200L, 3);
        
        // Mark item as deleted
        item.markAsDeleted();
        cartItemRepository.save(item);
        
        CartUpdateQuantityRequestDto request = new CartUpdateQuantityRequestDto(10);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cartUpdateQuantityService.updateItemQuantity(cart.getId(), item.getId(), request)
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
        
        CartUpdateQuantityRequestDto request1 = new CartUpdateQuantityRequestDto(8);
        CartUpdateQuantityRequestDto request2 = new CartUpdateQuantityRequestDto(3);
        CartUpdateQuantityRequestDto request3 = new CartUpdateQuantityRequestDto(15);

        // When
        cartUpdateQuantityService.updateItemQuantity(cart.getId(), item1.getId(), request1);
        cartUpdateQuantityService.updateItemQuantity(cart.getId(), item2.getId(), request2);
        cartUpdateQuantityService.updateItemQuantity(cart.getId(), item3.getId(), request3);

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
        
        CartUpdateQuantityRequestDto request = new CartUpdateQuantityRequestDto(12);

        // When
        cartUpdateQuantityService.updateItemQuantity(cart.getId(), item.getId(), request);

        // Then
        CartItem updatedItem = cartItemRepository.findById(item.getId()).orElseThrow();
        
        assertEquals(12, updatedItem.getQuantity()); // Quantity should be updated
        assertEquals(originalProductId, updatedItem.getProductId()); // Other properties preserved
        assertEquals(originalVariantId, updatedItem.getVariantId());
        assertEquals(originalCartId, updatedItem.getCart().getId());
        assertFalse(updatedItem.isDeleted());
    }
}