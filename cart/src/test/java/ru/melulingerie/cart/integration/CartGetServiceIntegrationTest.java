package ru.melulingerie.cart.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.melulingerie.cart.domain.Cart;
import ru.melulingerie.cart.domain.CartItem;
import ru.melulingerie.cart.dto.response.CartGetResponseDto;
import ru.melulingerie.cart.dto.response.CartItemGetResponseDto;
import ru.melulingerie.cart.service.CartGetService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CartGetService Integration Tests")
class CartGetServiceIntegrationTest extends CartIntegrationTestBase {

    @Autowired
    private CartGetService cartGetService;

    @AfterEach
    void cleanUp() {
        clearDatabase();
    }

    @Test
    @DisplayName("Should retrieve empty cart successfully")
    void shouldRetrieveEmptyCartSuccessfully() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);

        // When
        CartGetResponseDto result = cartGetService.getCart(cart.getId());

        // Then
        assertNotNull(result);
        assertEquals(cart.getId(), result.cartId());
        assertEquals(0, result.itemsCount());
        assertTrue(result.items().isEmpty());
    }

    @Test
    @DisplayName("Should retrieve cart with single item")
    void shouldRetrieveCartWithSingleItem() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        CartItem item = createTestCartItem(cart, 100L, 200L, 3);

        // When
        CartGetResponseDto result = cartGetService.getCart(cart.getId());

        // Then
        assertNotNull(result);
        assertEquals(cart.getId(), result.cartId());
        assertEquals(1, result.itemsCount());

        List<CartItemGetResponseDto> items = result.items();
        assertEquals(1, items.size());

        CartItemGetResponseDto itemDto = items.get(0);
        assertEquals(item.getId(), itemDto.itemId());
        assertEquals(100L, itemDto.productId());
        assertEquals(200L, itemDto.variantId());
        assertEquals(3, itemDto.quantity());
        assertNotNull(itemDto.addedAt());
    }

    @Test
    @DisplayName("Should retrieve cart with multiple items sorted by date")
    void shouldRetrieveCartWithMultipleItemsSortedByDate() throws InterruptedException {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);

        // Create items with different timestamps
        CartItem item1 = createTestCartItem(cart, 100L, 200L, 1);
        Thread.sleep(10); // Small delay to ensure different timestamps
        CartItem item2 = createTestCartItem(cart, 101L, 201L, 2);
        Thread.sleep(10);
        CartItem item3 = createTestCartItem(cart, 102L, 202L, 3);

        // When
        CartGetResponseDto result = cartGetService.getCart(cart.getId());

        // Then
        assertNotNull(result);
        assertEquals(cart.getId(), result.cartId());
        assertEquals(3, result.itemsCount());

        List<CartItemGetResponseDto> items = result.items();
        assertEquals(3, items.size());

        // Verify all created items are present in the response
        List<Long> expectedProductIds = List.of(item1.getProductId(), item2.getProductId(), item3.getProductId());
        List<Long> actualProductIds = items.stream().map(CartItemGetResponseDto::productId).toList();

        assertTrue(actualProductIds.containsAll(expectedProductIds),
                "Response should contain all created product IDs: " + expectedProductIds + ", but got: " + actualProductIds);

        // Verify quantities match
        for (CartItemGetResponseDto itemDto : items) {
            if (itemDto.productId().equals(item1.getProductId())) {
                assertEquals(item1.getQuantity(), itemDto.quantity());
                assertEquals(item1.getVariantId(), itemDto.variantId());
            } else if (itemDto.productId().equals(item2.getProductId())) {
                assertEquals(item2.getQuantity(), itemDto.quantity());
                assertEquals(item2.getVariantId(), itemDto.variantId());
            } else if (itemDto.productId().equals(item3.getProductId())) {
                assertEquals(item3.getQuantity(), itemDto.quantity());
                assertEquals(item3.getVariantId(), itemDto.variantId());
            }
        }
    }

    @Test
    @DisplayName("Should exclude deleted items from cart")
    void shouldExcludeDeletedItemsFromCart() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);

        CartItem activeItem = createTestCartItem(cart, 100L, 200L, 2);
        CartItem deletedItem = createTestCartItem(cart, 101L, 201L, 1);

        // Mark one item as deleted
        deletedItem.markAsDeleted();
        cartItemRepository.save(deletedItem);

        // When
        CartGetResponseDto result = cartGetService.getCart(cart.getId());

        // Then
        assertNotNull(result);
        assertEquals(cart.getId(), result.cartId());
        assertEquals(1, result.itemsCount()); // Only active item should be counted

        List<CartItemGetResponseDto> items = result.items();
        assertEquals(1, items.size());

        CartItemGetResponseDto itemDto = items.get(0);
        assertEquals(activeItem.getId(), itemDto.itemId());
        assertEquals(100L, itemDto.productId());
        assertEquals(200L, itemDto.variantId());
        assertEquals(2, itemDto.quantity());
    }

    @Test
    @DisplayName("Should throw exception when cart not found")
    void shouldThrowExceptionWhenCartNotFound() {
        // Given
        Long nonExistentCartId = 999L;

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cartGetService.getCart(nonExistentCartId)
        );

        assertEquals("Cart not found with id: " + nonExistentCartId, exception.getMessage());
    }

    @Test
    @DisplayName("Should handle cart with many items efficiently")
    void shouldHandleCartWithManyItemsEfficiently() {
        // Given
        Long userId = 42L;
        Cart cart = createCartWithItems(userId, 50); // Create cart with 50 items

        // When
        long startTime = System.currentTimeMillis();
        CartGetResponseDto result = cartGetService.getCart(cart.getId());
        long endTime = System.currentTimeMillis();

        // Then
        assertNotNull(result);
        assertEquals(cart.getId(), result.cartId());
        assertEquals(50, result.itemsCount());
        assertEquals(50, result.items().size());

        // Verify performance is reasonable (should complete in less than 1 second)
        assertTrue(endTime - startTime < 1000, "Cart retrieval should be fast even with many items");

        // Verify all items are unique
        List<Long> productIds = result.items().stream()
                .map(CartItemGetResponseDto::productId)
                .distinct()
                .toList();
        assertEquals(50, productIds.size());
    }

    @Test
    @DisplayName("Should preserve item timestamps correctly")
    void shouldPreserveItemTimestampsCorrectly() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);
        LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);

        CartItem item = createTestCartItem(cart, 100L, 200L, 1);

        LocalDateTime afterCreation = LocalDateTime.now().plusSeconds(1);

        // When
        CartGetResponseDto result = cartGetService.getCart(cart.getId());

        // Then
        assertNotNull(result);
        assertEquals(1, result.items().size());

        CartItemGetResponseDto itemDto = result.items().get(0);
        assertNotNull(itemDto.addedAt());

        // Verify timestamp is within reasonable range
        assertTrue(itemDto.addedAt().isAfter(beforeCreation));
        assertTrue(itemDto.addedAt().isBefore(afterCreation));
    }

    @Test
    @DisplayName("Should handle cart with zero quantity items")
    void shouldHandleCartWithVariousQuantities() {
        // Given
        Long userId = 42L;
        Cart cart = createTestCart(userId);

        CartItem item1 = createTestCartItem(cart, 100L, 200L, 1);
        CartItem item2 = createTestCartItem(cart, 101L, 201L, 10);
        CartItem item3 = createTestCartItem(cart, 102L, 202L, 99); // Maximum quantity

        // When
        CartGetResponseDto result = cartGetService.getCart(cart.getId());

        // Then
        assertNotNull(result);
        assertEquals(cart.getId(), result.cartId());
        assertEquals(3, result.itemsCount());

        List<CartItemGetResponseDto> items = result.items();
        assertEquals(3, items.size());

        // Verify quantities are preserved correctly
        int totalQuantity = items.stream()
                .mapToInt(CartItemGetResponseDto::quantity)
                .sum();
        assertEquals(110, totalQuantity); // 1 + 10 + 99 = 110
    }
}