package ru.melulingerie.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.melulingerie.exception.WishlistExceptions;
import ru.melulingerie.util.WishlistValidator;
import ru.melulingerie.wishlist.domain.Wishlist;
import ru.melulingerie.wishlist.domain.WishlistItem;
import ru.melulingerie.wishlist.repository.WishlistItemRepository;
import ru.melulingerie.wishlist.repository.WishlistRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WishlistItemRemoveServiceImpl Tests")
class WishlistItemRemoveServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;
    
    @Mock
    private WishlistItemRepository wishlistItemRepository;
    
    @Mock
    private WishlistValidator validationService;
    
    @InjectMocks
    private WishlistRemoveItemServiceImpl wishlistItemRemoveService;

    private static final Long WISHLIST_ID = 100L;
    private static final Long USER_ID = 1L;
    private static final Long ITEM_ID_1 = 1L;
    private static final Long ITEM_ID_2 = 2L;
    private static final Long ITEM_ID_3 = 3L;
    private static final Long INVALID_WISHLIST_ID = -1L;
    private static final Long INVALID_ITEM_ID = -1L;

    private Wishlist wishlist;
    private WishlistItem wishlistItem1;
    private WishlistItem wishlistItem2;
    private WishlistItem wishlistItem3;

    @BeforeEach
    void setUp() {
        wishlist = new Wishlist();
        wishlist.setId(WISHLIST_ID);
        wishlist.setUserId(USER_ID);

        wishlistItem1 = new WishlistItem();
        wishlistItem1.setId(ITEM_ID_1);
        wishlistItem1.setProductId(10L);
        wishlistItem1.setVariantId(20L);
        wishlistItem1.setWishlist(wishlist);
        wishlistItem1.setAddedAt(LocalDateTime.now());

        wishlistItem2 = new WishlistItem();
        wishlistItem2.setId(ITEM_ID_2);
        wishlistItem2.setProductId(11L);
        wishlistItem2.setVariantId(21L);
        wishlistItem2.setWishlist(wishlist);
        wishlistItem2.setAddedAt(LocalDateTime.now());

        wishlistItem3 = new WishlistItem();
        wishlistItem3.setId(ITEM_ID_3);
        wishlistItem3.setProductId(12L);
        wishlistItem3.setVariantId(22L);
        wishlistItem3.setWishlist(wishlist);
        wishlistItem3.setAddedAt(LocalDateTime.now());
        
        wishlist.setWishlistItems(List.of(wishlistItem1, wishlistItem2, wishlistItem3));
    }

    @Test
    @DisplayName("Должен успешно удалить один элемент из wishlist")
    void shouldSuccessfullyRemoveSingleItemFromWishlist() {
        // Given
        List<Long> itemIds = List.of(ITEM_ID_1);
        when(wishlistRepository.findByIdWithItems(WISHLIST_ID)).thenReturn(Optional.of(wishlist));

        // When
        assertDoesNotThrow(() -> wishlistItemRemoveService.removeWishlistItems(WISHLIST_ID, itemIds));

        // Then
        verify(validationService).validatePositiveIdOrThrow(WISHLIST_ID);
        verify(validationService).validatePositiveIdOrThrow(ITEM_ID_1);
        verify(wishlistRepository).findByIdWithItems(WISHLIST_ID);
        verify(wishlistItemRepository).deleteAll(List.of(wishlistItem1));
    }

    @Test
    @DisplayName("Должен успешно удалить несколько элементов из wishlist")
    void shouldSuccessfullyRemoveMultipleItemsFromWishlist() {
        // Given
        List<Long> itemIds = List.of(ITEM_ID_1, ITEM_ID_2);
        when(wishlistRepository.findByIdWithItems(WISHLIST_ID)).thenReturn(Optional.of(wishlist));

        // When
        assertDoesNotThrow(() -> wishlistItemRemoveService.removeWishlistItems(WISHLIST_ID, itemIds));

        // Then
        verify(validationService).validatePositiveIdOrThrow(WISHLIST_ID);
        verify(validationService).validatePositiveIdOrThrow(ITEM_ID_1);
        verify(validationService).validatePositiveIdOrThrow(ITEM_ID_2);
        verify(wishlistRepository).findByIdWithItems(WISHLIST_ID);
        verify(wishlistItemRepository).deleteAll(List.of(wishlistItem1, wishlistItem2));
    }

    @Test
    @DisplayName("Должен успешно удалить все элементы из wishlist")
    void shouldSuccessfullyRemoveAllItemsFromWishlist() {
        // Given
        List<Long> itemIds = List.of(ITEM_ID_1, ITEM_ID_2, ITEM_ID_3);
        when(wishlistRepository.findByIdWithItems(WISHLIST_ID)).thenReturn(Optional.of(wishlist));

        // When
        assertDoesNotThrow(() -> wishlistItemRemoveService.removeWishlistItems(WISHLIST_ID, itemIds));

        // Then
        verify(validationService).validatePositiveIdOrThrow(WISHLIST_ID);
        verify(validationService).validatePositiveIdOrThrow(ITEM_ID_1);
        verify(validationService).validatePositiveIdOrThrow(ITEM_ID_2);
        verify(validationService).validatePositiveIdOrThrow(ITEM_ID_3);
        verify(wishlistRepository).findByIdWithItems(WISHLIST_ID);
        verify(wishlistItemRepository).deleteAll(List.of(wishlistItem1, wishlistItem2, wishlistItem3));
    }

    @Test
    @DisplayName("Должен ничего не делать если список элементов пустой")
    void shouldDoNothingWhenItemListIsEmpty() {
        // Given
        List<Long> emptyItemIds = List.of();

        // When
        assertDoesNotThrow(() -> wishlistItemRemoveService.removeWishlistItems(WISHLIST_ID, emptyItemIds));

        // Then
        verify(validationService).validatePositiveIdOrThrow(WISHLIST_ID);
        verify(wishlistRepository, never()).findByIdWithItems(any());
        verify(wishlistItemRepository, never()).deleteAll(any());
    }

    @Test
    @DisplayName("Должен ничего не делать если список элементов null")
    void shouldDoNothingWhenItemListIsNull() {
        // When
        assertDoesNotThrow(() -> wishlistItemRemoveService.removeWishlistItems(WISHLIST_ID, null));

        // Then
        verify(validationService).validatePositiveIdOrThrow(WISHLIST_ID);
        verify(wishlistRepository, never()).findByIdWithItems(any());
        verify(wishlistItemRepository, never()).deleteAll(any());
    }

    @Test
    @DisplayName("Должен выбросить исключение для некорректного wishlistId")
    void shouldThrowExceptionForInvalidWishlistId() {
        // Given
        doThrow(new IllegalArgumentException("Invalid wishlistId"))
                .when(validationService).validatePositiveIdOrThrow(INVALID_WISHLIST_ID);

        // When & Then
        assertThatThrownBy(() -> wishlistItemRemoveService.removeWishlistItems(INVALID_WISHLIST_ID, List.of(ITEM_ID_1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid wishlistId");

        verify(validationService).validatePositiveIdOrThrow(INVALID_WISHLIST_ID);
        verify(wishlistRepository, never()).findByIdWithItems(any());
        verify(wishlistItemRepository, never()).deleteAll(any());
    }

    @Test
    @DisplayName("Должен выбросить исключение для некорректного itemId")
    void shouldThrowExceptionForInvalidItemId() {
        // Given
        doNothing().when(validationService).validatePositiveIdOrThrow(WISHLIST_ID);
        doThrow(new IllegalArgumentException("Invalid itemId"))
                .when(validationService).validatePositiveIdOrThrow(INVALID_ITEM_ID);

        // When & Then
        assertThatThrownBy(() -> wishlistItemRemoveService.removeWishlistItems(WISHLIST_ID, List.of(INVALID_ITEM_ID)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid itemId");

        verify(validationService).validatePositiveIdOrThrow(WISHLIST_ID);
        verify(validationService).validatePositiveIdOrThrow(INVALID_ITEM_ID);
        verify(wishlistRepository, never()).findByIdWithItems(any());
        verify(wishlistItemRepository, never()).deleteAll(any());
    }

    @Test
    @DisplayName("Должен выбросить исключение, если wishlist не найден")
    void shouldThrowExceptionWhenWishlistNotFound() {
        // Given
        List<Long> itemIds = List.of(ITEM_ID_1);
        when(wishlistRepository.findByIdWithItems(WISHLIST_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> wishlistItemRemoveService.removeWishlistItems(WISHLIST_ID, itemIds))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Wishlist not found with id: " + WISHLIST_ID);

        verify(validationService).validatePositiveIdOrThrow(WISHLIST_ID);
        verify(validationService).validatePositiveIdOrThrow(ITEM_ID_1);
        verify(wishlistRepository).findByIdWithItems(WISHLIST_ID);
        verify(wishlistItemRepository, never()).deleteAll(any());
    }

    @Test
    @DisplayName("Должен выбросить исключение если элемент не найден в wishlist")
    void shouldThrowExceptionWhenItemNotFoundInWishlist() {
        // Given
        Long nonExistentItemId = 999L;
        List<Long> itemIds = List.of(nonExistentItemId);
        when(wishlistRepository.findByIdWithItems(WISHLIST_ID)).thenReturn(Optional.of(wishlist));

        // When & Then
        assertThatThrownBy(() -> wishlistItemRemoveService.removeWishlistItems(WISHLIST_ID, itemIds))
                .isInstanceOf(WishlistExceptions.WishlistItemNotFoundException.class);

        verify(validationService).validatePositiveIdOrThrow(WISHLIST_ID);
        verify(validationService).validatePositiveIdOrThrow(nonExistentItemId);
        verify(wishlistRepository).findByIdWithItems(WISHLIST_ID);
        verify(wishlistItemRepository, never()).deleteAll(any());
    }

    @Test
    @DisplayName("Должен выбросить исключение если часть элементов не найдена в wishlist")
    void shouldThrowExceptionWhenSomeItemsNotFoundInWishlist() {
        // Given
        Long nonExistentItemId = 999L;
        List<Long> itemIds = List.of(ITEM_ID_1, nonExistentItemId);
        when(wishlistRepository.findByIdWithItems(WISHLIST_ID)).thenReturn(Optional.of(wishlist));

        // When & Then
        assertThatThrownBy(() -> wishlistItemRemoveService.removeWishlistItems(WISHLIST_ID, itemIds))
                .isInstanceOf(WishlistExceptions.WishlistItemNotFoundException.class);

        verify(validationService).validatePositiveIdOrThrow(WISHLIST_ID);
        verify(validationService).validatePositiveIdOrThrow(ITEM_ID_1);
        verify(validationService).validatePositiveIdOrThrow(nonExistentItemId);
        verify(wishlistRepository).findByIdWithItems(WISHLIST_ID);
        verify(wishlistItemRepository, never()).deleteAll(any());
    }

    @Test
    @DisplayName("Должен правильно работать с различными wishlist")
    void shouldWorkWithDifferentWishlists() {
        // Given
        Long anotherWishlistId = 200L;
        Long anotherUserId = 2L;
        Long anotherItemId = 4L;
        
        Wishlist anotherWishlist = new Wishlist();
        anotherWishlist.setId(anotherWishlistId);
        anotherWishlist.setUserId(anotherUserId);
        
        WishlistItem anotherItem = new WishlistItem();
        anotherItem.setId(anotherItemId);
        anotherItem.setWishlist(anotherWishlist);
        
        anotherWishlist.setWishlistItems(List.of(anotherItem));
        
        List<Long> itemIds = List.of(anotherItemId);
        when(wishlistRepository.findByIdWithItems(anotherWishlistId)).thenReturn(Optional.of(anotherWishlist));

        // When
        assertDoesNotThrow(() -> wishlistItemRemoveService.removeWishlistItems(anotherWishlistId, itemIds));

        // Then
        verify(validationService).validatePositiveIdOrThrow(anotherWishlistId);
        verify(validationService).validatePositiveIdOrThrow(anotherItemId);
        verify(wishlistRepository).findByIdWithItems(anotherWishlistId);
        verify(wishlistItemRepository).deleteAll(List.of(anotherItem));
    }

    @Test
    @DisplayName("Должен вызвать методы в правильном порядке")
    void shouldCallMethodsInCorrectOrder() {
        // Given
        List<Long> itemIds = List.of(ITEM_ID_1, ITEM_ID_2);
        when(wishlistRepository.findByIdWithItems(WISHLIST_ID)).thenReturn(Optional.of(wishlist));

        // When
        wishlistItemRemoveService.removeWishlistItems(WISHLIST_ID, itemIds);

        // Then
        var inOrder = inOrder(validationService, wishlistRepository, wishlistItemRepository);
        inOrder.verify(validationService).validatePositiveIdOrThrow(WISHLIST_ID);
        inOrder.verify(validationService).validatePositiveIdOrThrow(ITEM_ID_1);
        inOrder.verify(validationService).validatePositiveIdOrThrow(ITEM_ID_2);
        inOrder.verify(wishlistRepository).findByIdWithItems(WISHLIST_ID);
        inOrder.verify(wishlistItemRepository).deleteAll(List.of(wishlistItem1, wishlistItem2));
    }
}