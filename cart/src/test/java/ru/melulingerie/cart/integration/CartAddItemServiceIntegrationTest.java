package ru.melulingerie.cart.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.melulingerie.cart.domain.Cart;
import ru.melulingerie.cart.domain.CartItem;
import ru.melulingerie.cart.dto.request.CartAddItemRequestDto;
import ru.melulingerie.cart.dto.response.CartAddItemResponseDto;
import ru.melulingerie.cart.exception.CartExceptions;
import ru.melulingerie.cart.service.CartAddItemService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CartAddItemService Integration Tests")
class CartAddItemServiceIntegrationTest extends CartIntegrationTestBase {

    @Autowired
    private CartAddItemService cartAddItemService;

    @AfterEach
    void cleanUp() {
        clearDatabase();
    }

    @Test
    @DisplayName("Should add new item to empty cart")
    void shouldAddNewItemToEmptyCart() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        CartAddItemRequestDto request = new CartAddItemRequestDto(100L, 200L, 3);

        // When
        CartAddItemResponseDto result = cartAddItemService.addCartItem(cart.getId(), request);

        // Then
        assertNotNull(result);
        assertEquals("Added to cart", result.message());

        // Verify database state
        Optional<Cart> savedCart = cartRepository.findCartByIdWithItemsSortedByDate(cart.getId());
        assertTrue(savedCart.isPresent());

        List<CartItem> cartItems = savedCart.get().getCartItems();
        assertEquals(1, cartItems.size());

        CartItem savedItem = cartItems.getFirst();
        assertEquals(100L, savedItem.getProductId());
        assertEquals(200L, savedItem.getVariantId());
        assertEquals(3, savedItem.getQuantity());
        assertFalse(savedItem.isDeleted());
    }

    @Test
    @DisplayName("Should update quantity when adding existing product")
    void shouldUpdateQuantityWhenAddingExistingProduct() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        CartItem existingItem = createTestCartItem(cart, 100L, 200L, 2);

        CartAddItemRequestDto request = new CartAddItemRequestDto(100L, 200L, 3);

        // When
        CartAddItemResponseDto result = cartAddItemService.addCartItem(cart.getId(), request);

        // Then
        assertNotNull(result);
        assertEquals("Quantity updated in cart", result.message());
        assertEquals(existingItem.getId(), result.cartItemId());

        // Verify database state
        CartItem updatedItem = cartItemRepository.findById(existingItem.getId()).orElseThrow();
        assertEquals(5, updatedItem.getQuantity()); // 2 + 3 = 5
    }

    @Test
    @DisplayName("Should handle adding multiple different items")
    void shouldHandleAddingMultipleDifferentItems() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);

        CartAddItemRequestDto request1 = new CartAddItemRequestDto(100L, 200L, 2);
        CartAddItemRequestDto request2 = new CartAddItemRequestDto(101L, 201L, 1);
        CartAddItemRequestDto request3 = new CartAddItemRequestDto(102L, 202L, 4);

        // When
        CartAddItemResponseDto result1 = cartAddItemService.addCartItem(cart.getId(), request1);
        CartAddItemResponseDto result2 = cartAddItemService.addCartItem(cart.getId(), request2);
        CartAddItemResponseDto result3 = cartAddItemService.addCartItem(cart.getId(), request3);

        // Then
        assertEquals("Added to cart", result1.message());
        assertEquals("Added to cart", result2.message());
        assertEquals("Added to cart", result3.message());

        // Verify database state
        Optional<Cart> savedCart = cartRepository.findCartByIdWithItemsSortedByDate(cart.getId());
        assertTrue(savedCart.isPresent());

        List<CartItem> cartItems = savedCart.get().getCartItems();
        assertEquals(3, cartItems.size());

        // Verify each item
        CartItem item1 = cartItems.stream()
                .filter(item -> item.getProductId().equals(100L))
                .findFirst().orElseThrow();
        assertEquals(2, item1.getQuantity());

        CartItem item2 = cartItems.stream()
                .filter(item -> item.getProductId().equals(101L))
                .findFirst().orElseThrow();
        assertEquals(1, item2.getQuantity());

        CartItem item3 = cartItems.stream()
                .filter(item -> item.getProductId().equals(102L))
                .findFirst().orElseThrow();
        assertEquals(4, item3.getQuantity());
    }

    @Test
    @DisplayName("Should throw exception when cart not found")
    void shouldThrowExceptionWhenCartNotFound() {
        // Given
        Long nonExistentCartId = 999L;
        CartAddItemRequestDto request = new CartAddItemRequestDto(100L, 200L, 1);

        // When & Then
        assertThrows(CartExceptions.CartNotFoundException.class,
                () -> cartAddItemService.addCartItem(nonExistentCartId, request));

        // Verify no items were created
        List<CartItem> allItems = cartItemRepository.findAll();
        assertTrue(allItems.isEmpty());
    }

    @Test
    @DisplayName("Should throw exception when quantity exceeds maximum per item")
    void shouldThrowExceptionWhenQuantityExceedsMaximumPerItem() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        CartItem existingItem = createTestCartItem(cart, 100L, 200L, 95);

        // Trying to add 10 more items (95 + 10 = 105, which exceeds max of 99)
        CartAddItemRequestDto request = new CartAddItemRequestDto(100L, 200L, 10);

        // When & Then
        assertThrows(CartExceptions.InvalidQuantityException.class,
                () -> cartAddItemService.addCartItem(cart.getId(), request));

        // Verify original quantity unchanged
        CartItem unchangedItem = cartItemRepository.findById(existingItem.getId()).orElseThrow();
        assertEquals(95, unchangedItem.getQuantity());
    }

    @Test
    @DisplayName("Should throw exception when cart reaches maximum items limit")
    void shouldThrowExceptionWhenCartReachesMaximumItemsLimit() {
        // Given
        Long userId = 42L;
        Cart cart = createCartWithItems(userId, 100); // Create cart with max items (100)

        CartAddItemRequestDto request = new CartAddItemRequestDto(999L, 999L, 1);

        // When & Then
        assertThrows(CartExceptions.CartFullException.class,
                () -> cartAddItemService.addCartItem(cart.getId(), request));

        // Verify no additional items were created
        List<CartItem> allItems = cartItemRepository.findAll();
        assertEquals(100, allItems.size());
    }

    @Test
    @DisplayName("Should handle concurrent additions of same product")
    void shouldHandleConcurrentAdditionsOfSameProduct() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        CartAddItemRequestDto request1 = new CartAddItemRequestDto(100L, 200L, 2);
        CartAddItemRequestDto request2 = new CartAddItemRequestDto(100L, 200L, 3);

        // When - simulate concurrent additions
        CartAddItemResponseDto result1 = cartAddItemService.addCartItem(cart.getId(), request1);
        CartAddItemResponseDto result2 = cartAddItemService.addCartItem(cart.getId(), request2);

        // Then
        assertEquals("Added to cart", result1.message());
        assertEquals("Quantity updated in cart", result2.message());

        // Verify only one item exists with correct total quantity
        List<CartItem> cartItems = cartItemRepository.findAll();
        assertEquals(1, cartItems.size());

        CartItem finalItem = cartItems.get(0);
        assertEquals(5, finalItem.getQuantity()); // 2 + 3 = 5
        assertEquals(100L, finalItem.getProductId());
        assertEquals(200L, finalItem.getVariantId());
    }
}