package ru.melulingerie.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.melulingerie.dto.WishlistAddItemRequestDto;
import ru.melulingerie.dto.WishlistAddItemResponseDto;
import ru.melulingerie.exception.AggregatedValidationException;
import ru.melulingerie.util.WishlistValidator;
import ru.melulingerie.domain.Wishlist;
import ru.melulingerie.domain.WishlistItem;
import ru.melulingerie.repository.WishlistItemRepository;
import ru.melulingerie.repository.WishlistRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WishlistItemAddServiceImpl Tests")
class WishlistItemAddServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;
    
    @Mock
    private WishlistItemRepository wishlistItemRepository;
    
    @Mock
    private WishlistValidator wishlistValidator;
    
    @InjectMocks
    private WishlistAddItemServiceImpl wishlistItemAddService;

    private static final Long WISHLIST_ID = 100L;
    private static final Long USER_ID = 1L;
    private static final Long PRODUCT_ID = 10L;
    private static final Long VARIANT_ID = 20L;
    private static final Long ITEM_ID = 1L;
    private static final int MAX_ITEMS = 200;

    private Wishlist wishlist;
    private WishlistAddItemRequestDto validRequest;
    private WishlistItem savedItem;

    @BeforeEach
    void setUp() {
        wishlist = new Wishlist();
        wishlist.setId(WISHLIST_ID);
        wishlist.setUserId(USER_ID);

        validRequest = new WishlistAddItemRequestDto(PRODUCT_ID, VARIANT_ID);

        savedItem = new WishlistItem();
        savedItem.setId(ITEM_ID);
        savedItem.setProductId(PRODUCT_ID);
        savedItem.setVariantId(VARIANT_ID);
        savedItem.setWishlist(wishlist);
        savedItem.setAddedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Должен успешно добавить элемент в wishlist")
    void shouldSuccessfullyAddItemToWishlist() {
        // Given
        wishlist.setWishlistItems(List.of());
        when(wishlistRepository.findByIdWithAllItems(WISHLIST_ID)).thenReturn(Optional.of(wishlist));
        when(wishlistItemRepository.save(any(WishlistItem.class))).thenAnswer(invocation -> {
            WishlistItem item = invocation.getArgument(0);
            item.setId(ITEM_ID);
            return item;
        });

        // When
        WishlistAddItemResponseDto result = wishlistItemAddService.addWishlistItem(WISHLIST_ID, validRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.wishlistItemId()).isEqualTo(ITEM_ID);
        assertThat(result.message()).isEqualTo("Added to wishlist");

        verify(wishlistValidator).validatePositiveIdOrThrow(WISHLIST_ID);
        verify(wishlistRepository).findByIdWithAllItems(WISHLIST_ID);
        verify(wishlistValidator).validateAddWishlist(USER_ID, validRequest, wishlist, 0, MAX_ITEMS, false);
        
        ArgumentCaptor<WishlistItem> itemCaptor = ArgumentCaptor.forClass(WishlistItem.class);
        verify(wishlistItemRepository).save(itemCaptor.capture());
        
        WishlistItem capturedItem = itemCaptor.getValue();
        assertThat(capturedItem.getWishlist()).isEqualTo(wishlist);
        assertThat(capturedItem.getProductId()).isEqualTo(PRODUCT_ID);
        assertThat(capturedItem.getVariantId()).isEqualTo(VARIANT_ID);
        assertThat(capturedItem.getAddedAt()).isNotNull();
    }

    @Test
    @DisplayName("Должен выбросить исключение для некорректного wishlistId")
    void shouldThrowExceptionForInvalidWishlistId() {
        // Given
        Long invalidWishlistId = -1L;
        doThrow(new IllegalArgumentException("Invalid wishlistId"))
                .when(wishlistValidator).validatePositiveIdOrThrow(invalidWishlistId);

        // When & Then
        assertThatThrownBy(() -> wishlistItemAddService.addWishlistItem(invalidWishlistId, validRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid wishlistId");

        verify(wishlistValidator).validatePositiveIdOrThrow(invalidWishlistId);
        verify(wishlistRepository, never()).findByIdWithAllItems(any());
        verify(wishlistItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен выбросить исключение, если wishlist не найден")
    void shouldThrowExceptionWhenWishlistNotFound() {
        // Given
        when(wishlistRepository.findByIdWithAllItems(WISHLIST_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> wishlistItemAddService.addWishlistItem(WISHLIST_ID, validRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incorrect wishlistId: " + WISHLIST_ID);

        verify(wishlistValidator).validatePositiveIdOrThrow(WISHLIST_ID);
        verify(wishlistRepository).findByIdWithAllItems(WISHLIST_ID);
        verify(wishlistValidator, never()).validateAddWishlist(any(), any(), any(), anyInt(), anyInt(), anyBoolean());
        verify(wishlistItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен учитывать существующие элементы при проверке лимита")
    void shouldConsiderExistingItemsWhenCheckingLimit() {
        // Given
        WishlistItem existingItem1 = new WishlistItem();
        existingItem1.setProductId(11L);
        existingItem1.setVariantId(21L);
        
        WishlistItem existingItem2 = new WishlistItem();
        existingItem2.setProductId(12L);
        existingItem2.setVariantId(22L);
        
        WishlistItem existingItem3 = new WishlistItem();
        existingItem3.setProductId(13L);
        existingItem3.setVariantId(23L);
        
        wishlist.setWishlistItems(List.of(existingItem1, existingItem2, existingItem3));
        when(wishlistRepository.findByIdWithAllItems(WISHLIST_ID)).thenReturn(Optional.of(wishlist));
        when(wishlistItemRepository.save(any(WishlistItem.class))).thenAnswer(invocation -> {
            WishlistItem item = invocation.getArgument(0);
            item.setId(ITEM_ID);
            return item;
        });

        // When
        WishlistAddItemResponseDto result = wishlistItemAddService.addWishlistItem(WISHLIST_ID, validRequest);

        // Then
        assertThat(result).isNotNull();
        verify(wishlistValidator).validateAddWishlist(USER_ID, validRequest, wishlist, 3, MAX_ITEMS, false);
        verify(wishlistItemRepository).save(any(WishlistItem.class));
    }

    @Test
    @DisplayName("Должен обнаружить дублирующий элемент")
    void shouldDetectDuplicateItem() {
        // Given
        WishlistItem duplicateItem = new WishlistItem();
        duplicateItem.setProductId(PRODUCT_ID);
        duplicateItem.setVariantId(VARIANT_ID);
        
        wishlist.setWishlistItems(List.of(duplicateItem));
        when(wishlistRepository.findByIdWithAllItems(WISHLIST_ID)).thenReturn(Optional.of(wishlist));
        when(wishlistItemRepository.save(any(WishlistItem.class))).thenAnswer(invocation -> {
            WishlistItem item = invocation.getArgument(0);
            item.setId(ITEM_ID);
            return item;
        });

        // When
        WishlistAddItemResponseDto result = wishlistItemAddService.addWishlistItem(WISHLIST_ID, validRequest);

        // Then
        assertThat(result).isNotNull();
        verify(wishlistValidator).validateAddWishlist(USER_ID, validRequest, wishlist, 1, MAX_ITEMS, true);
        verify(wishlistItemRepository).save(any(WishlistItem.class));
    }

    @Test
    @DisplayName("Должен выбросить исключение при ошибке валидации")
    void shouldThrowExceptionOnValidationError() {
        // Given
        wishlist.setWishlistItems(List.of());
        when(wishlistRepository.findByIdWithAllItems(WISHLIST_ID)).thenReturn(Optional.of(wishlist));
        
        doThrow(new AggregatedValidationException(List.of("Validation error")))
                .when(wishlistValidator).validateAddWishlist(USER_ID, validRequest, wishlist, 0, MAX_ITEMS, false);

        // When & Then
        assertThatThrownBy(() -> wishlistItemAddService.addWishlistItem(WISHLIST_ID, validRequest))
                .isInstanceOf(AggregatedValidationException.class);

        verify(wishlistValidator).validatePositiveIdOrThrow(WISHLIST_ID);
        verify(wishlistRepository).findByIdWithAllItems(WISHLIST_ID);
        verify(wishlistValidator).validateAddWishlist(USER_ID, validRequest, wishlist, 0, MAX_ITEMS, false);
        verify(wishlistItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен выбросить исключение при нарушении целостности данных")
    void shouldThrowExceptionOnDataIntegrityViolation() {
        // Given
        wishlist.setWishlistItems(List.of());
        when(wishlistRepository.findByIdWithAllItems(WISHLIST_ID)).thenReturn(Optional.of(wishlist));
        when(wishlistItemRepository.save(any(WishlistItem.class)))
                .thenThrow(new DataIntegrityViolationException("Constraint violation"));

        // When & Then
        assertThatThrownBy(() -> wishlistItemAddService.addWishlistItem(WISHLIST_ID, validRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Item already exists in wishlist");

        verify(wishlistValidator).validatePositiveIdOrThrow(WISHLIST_ID);
        verify(wishlistRepository).findByIdWithAllItems(WISHLIST_ID);
        verify(wishlistValidator).validateAddWishlist(USER_ID, validRequest, wishlist, 0, MAX_ITEMS, false);
        verify(wishlistItemRepository).save(any(WishlistItem.class));
    }

    @Test
    @DisplayName("Должен правильно создать WishlistItem с текущим временем")
    void shouldCreateWishlistItemWithCurrentTime() {
        // Given
        LocalDateTime beforeCall = LocalDateTime.now().minusSeconds(1);
        wishlist.setWishlistItems(List.of());
        when(wishlistRepository.findByIdWithAllItems(WISHLIST_ID)).thenReturn(Optional.of(wishlist));
        when(wishlistItemRepository.save(any(WishlistItem.class))).thenAnswer(invocation -> {
            WishlistItem item = invocation.getArgument(0);
            item.setId(ITEM_ID);
            return item;
        });

        // When
        wishlistItemAddService.addWishlistItem(WISHLIST_ID, validRequest);
        LocalDateTime afterCall = LocalDateTime.now().plusSeconds(1);

        // Then
        ArgumentCaptor<WishlistItem> itemCaptor = ArgumentCaptor.forClass(WishlistItem.class);
        verify(wishlistItemRepository).save(itemCaptor.capture());
        
        WishlistItem capturedItem = itemCaptor.getValue();
        assertThat(capturedItem.getAddedAt()).isAfter(beforeCall);
        assertThat(capturedItem.getAddedAt()).isBefore(afterCall);
    }

    @Test
    @DisplayName("Должен правильно работать с максимальным количеством элементов")
    void shouldHandleMaxItemsLimit() {
        // Given
        List<WishlistItem> manyItems = new java.util.ArrayList<>();
        for (int i = 0; i < 199; i++) {
            WishlistItem item = new WishlistItem();
            item.setProductId((long) (i + 100));
            item.setVariantId((long) (i + 200));
            manyItems.add(item);
        }
        wishlist.setWishlistItems(manyItems);
        when(wishlistRepository.findByIdWithAllItems(WISHLIST_ID)).thenReturn(Optional.of(wishlist));
        when(wishlistItemRepository.save(any(WishlistItem.class))).thenAnswer(invocation -> {
            WishlistItem item = invocation.getArgument(0);
            item.setId(ITEM_ID);
            return item;
        });

        // When
        WishlistAddItemResponseDto result = wishlistItemAddService.addWishlistItem(WISHLIST_ID, validRequest);

        // Then
        assertThat(result).isNotNull();
        verify(wishlistValidator).validateAddWishlist(USER_ID, validRequest, wishlist, 199, MAX_ITEMS, false);
    }

    @Test
    @DisplayName("Должен вызвать методы в правильном порядке")
    void shouldCallMethodsInCorrectOrder() {
        // Given
        wishlist.setWishlistItems(List.of());
        when(wishlistRepository.findByIdWithAllItems(WISHLIST_ID)).thenReturn(Optional.of(wishlist));
        when(wishlistItemRepository.save(any(WishlistItem.class))).thenAnswer(invocation -> {
            WishlistItem item = invocation.getArgument(0);
            item.setId(ITEM_ID);
            return item;
        });

        // When
        wishlistItemAddService.addWishlistItem(WISHLIST_ID, validRequest);

        // Then
        var inOrder = inOrder(wishlistValidator, wishlistRepository, wishlistItemRepository);
        inOrder.verify(wishlistValidator).validatePositiveIdOrThrow(WISHLIST_ID);
        inOrder.verify(wishlistRepository).findByIdWithAllItems(WISHLIST_ID);
        inOrder.verify(wishlistValidator).validateAddWishlist(USER_ID, validRequest, wishlist, 0, MAX_ITEMS, false);
        inOrder.verify(wishlistItemRepository).save(any(WishlistItem.class));
    }
}