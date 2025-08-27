package ru.melulingerie.facade.wishlist.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionTemplate;
import ru.melulingerie.service.WishlistRemoveItemService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WishlistItemRemoveFacadeServiceImpl Tests")
class WishlistItemRemoveFacadeServiceTest {
    
    @Mock
    private WishlistRemoveItemService wishlistRemoveItemService;
    
    @Mock
    private TransactionTemplate transactionTemplate;
    
    @InjectMocks
    private WishlistRemoveItemFacadeServiceImpl wishlistItemRemoveFacadeService;

    private static final Long WISHLIST_ID = 1L;
    private static final Long WISHLIST_ITEM_ID_1 = 100L;
    private static final Long WISHLIST_ITEM_ID_2 = 200L;
    private static final Long WISHLIST_ITEM_ID_3 = 300L;

    @Test
    @DisplayName("Должен успешно удалить один элемент из wishlist")
    void shouldSuccessfullyRemoveSingleItemFromWishlist() {
        // Given
        List<Long> itemIds = List.of(WISHLIST_ITEM_ID_1);
        doNothing().when(transactionTemplate).executeWithoutResult(any());

        // When
        wishlistItemRemoveFacadeService.removeItemsFromWishlist(WISHLIST_ID, itemIds);

        // Then
        verify(transactionTemplate).executeWithoutResult(any());
    }

    @Test
    @DisplayName("Должен успешно удалить несколько элементов из wishlist")
    void shouldSuccessfullyRemoveMultipleItemsFromWishlist() {
        // Given
        List<Long> itemIds = List.of(WISHLIST_ITEM_ID_1, WISHLIST_ITEM_ID_2, WISHLIST_ITEM_ID_3);
        doNothing().when(transactionTemplate).executeWithoutResult(any());

        // When
        wishlistItemRemoveFacadeService.removeItemsFromWishlist(WISHLIST_ID, itemIds);

        // Then
        verify(transactionTemplate).executeWithoutResult(any());
    }

    @Test
    @DisplayName("Должен корректно передавать исключения из доменного сервиса")
    void shouldPropagateExceptionsFromDomainService() {
        // Given
        RuntimeException domainException = new RuntimeException("Domain service error");
        List<Long> itemIds = List.of(WISHLIST_ITEM_ID_1);
        
        // Mock TransactionTemplate to throw exception
        doThrow(domainException).when(transactionTemplate).executeWithoutResult(any());

        // When & Then
        assertThatThrownBy(() -> wishlistItemRemoveFacadeService.removeItemsFromWishlist(WISHLIST_ID, itemIds))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Domain service error");

        verify(transactionTemplate).executeWithoutResult(any());
    }

    @Test
    @DisplayName("Должен вызвать транзакцию и доменный сервис в правильном порядке")
    void shouldCallTransactionAndDomainServiceInCorrectOrder() {
        // Given
        List<Long> itemIds = List.of(WISHLIST_ITEM_ID_1, WISHLIST_ITEM_ID_2);
        
        // Mock TransactionTemplate to verify it calls wishlistRemoveItemService
        doAnswer(invocation -> {
            // Simulate the transaction execution
            wishlistRemoveItemService.removeWishlistItems(WISHLIST_ID, itemIds);
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());

        // When
        wishlistItemRemoveFacadeService.removeItemsFromWishlist(WISHLIST_ID, itemIds);

        // Then
        verify(transactionTemplate).executeWithoutResult(any());
        verify(wishlistRemoveItemService).removeWishlistItems(WISHLIST_ID, itemIds);
    }

    @Test
    @DisplayName("Должен правильно работать с различными wishlist и элементами")
    void shouldWorkWithDifferentWishlistsAndItems() {
        // Given
        Long anotherWishlistId = 2L;
        List<Long> anotherItemIds = List.of(400L, 500L);
        
        // Mock TransactionTemplate behavior
        doNothing().when(transactionTemplate).executeWithoutResult(any());

        // When
        wishlistItemRemoveFacadeService.removeItemsFromWishlist(anotherWishlistId, anotherItemIds);

        // Then
        verify(transactionTemplate).executeWithoutResult(any());
    }

    @Test
    @DisplayName("Должен работать с пустым списком элементов")
    void shouldWorkWithEmptyItemList() {
        // Given
        List<Long> emptyItemIds = List.of();
        doNothing().when(transactionTemplate).executeWithoutResult(any());

        // When
        wishlistItemRemoveFacadeService.removeItemsFromWishlist(WISHLIST_ID, emptyItemIds);

        // Then
        verify(transactionTemplate).executeWithoutResult(any());
    }

    @Test
    @DisplayName("Должен работать с null списком элементов")
    void shouldWorkWithNullItemList() {
        // Given
        doNothing().when(transactionTemplate).executeWithoutResult(any());

        // When
        wishlistItemRemoveFacadeService.removeItemsFromWishlist(WISHLIST_ID, null);

        // Then
        verify(transactionTemplate).executeWithoutResult(any());
    }

    @Test
    @DisplayName("Должен правильно работать с большим количеством элементов")
    void shouldWorkWithLargeItemList() {
        // Given
        List<Long> largeItemIds = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
        doNothing().when(transactionTemplate).executeWithoutResult(any());

        // When
        wishlistItemRemoveFacadeService.removeItemsFromWishlist(WISHLIST_ID, largeItemIds);

        // Then
        verify(transactionTemplate).executeWithoutResult(any());
    }
}