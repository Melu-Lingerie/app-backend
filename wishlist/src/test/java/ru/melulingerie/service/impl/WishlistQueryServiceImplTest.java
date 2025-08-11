package ru.melulingerie.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.melulingerie.dto.WishlistItemGetResponseDto;
import ru.melulingerie.dto.WishlistGetResponseDto;
import ru.melulingerie.util.WishlistValidator;
import ru.melulingerie.wishlist.domain.Wishlist;
import ru.melulingerie.wishlist.domain.WishlistItem;
import ru.melulingerie.wishlist.repository.WishlistRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WishlistGetServiceImpl Tests")
class WishlistQueryServiceImplTest {

    @Mock
    private WishlistRepository wishlistRepository;
    
    @Mock
    private WishlistValidator validationService;
    
    @InjectMocks
    private WishlistGetServiceImpl wishlistGetService;

    private static final Long WISHLIST_ID = 100L;
    private static final Long USER_ID = 1L;
    private static final Long INVALID_WISHLIST_ID = -1L;

    private Wishlist wishlist;
    private WishlistItem wishlistItem1;
    private WishlistItem wishlistItem2;

    @BeforeEach
    void setUp() {
        wishlist = new Wishlist();
        wishlist.setId(WISHLIST_ID);
        wishlist.setUserId(USER_ID);

        wishlistItem1 = new WishlistItem();
        wishlistItem1.setId(1L);
        wishlistItem1.setProductId(10L);
        wishlistItem1.setVariantId(20L);
        wishlistItem1.setAddedAt(LocalDateTime.now().minusDays(1));
        wishlistItem1.setWishlist(wishlist);

        wishlistItem2 = new WishlistItem();
        wishlistItem2.setId(2L);
        wishlistItem2.setProductId(11L);
        wishlistItem2.setVariantId(21L);
        wishlistItem2.setAddedAt(LocalDateTime.now());
        wishlistItem2.setWishlist(wishlist);
    }

    @Test
    @DisplayName("Должен успешно вернуть wishlist с элементами")
    void shouldReturnWishlistWithItems() {
        // Given
        List<WishlistItem> wishlistItems = List.of(wishlistItem1, wishlistItem2);
        wishlist.setWishlistItems(wishlistItems);
        
        when(wishlistRepository.findByIdWithItems(WISHLIST_ID)).thenReturn(Optional.of(wishlist));

        // When
        WishlistGetResponseDto result = wishlistGetService.getWishlist(WISHLIST_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.wishlistId()).isEqualTo(WISHLIST_ID);
        assertThat(result.items()).hasSize(2);
        assertThat(result.itemsCount()).isEqualTo(2);
        
        WishlistItemGetResponseDto firstItem = result.items().get(0);
        assertThat(firstItem.id()).isEqualTo(1L);
        assertThat(firstItem.productId()).isEqualTo(10L);
        assertThat(firstItem.variantId()).isEqualTo(20L);
        assertThat(firstItem.addedAt()).isEqualTo(wishlistItem1.getAddedAt());

        verify(validationService).validatePositiveIdOrThrow(WISHLIST_ID);
        verify(wishlistRepository).findByIdWithItems(WISHLIST_ID);
    }

    @Test
    @DisplayName("Должен выбросить исключение, когда wishlist не найден")
    void shouldThrowExceptionWhenWishlistNotFound() {
        // Given
        when(wishlistRepository.findByIdWithItems(WISHLIST_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> wishlistGetService.getWishlist(WISHLIST_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Wishlist not found with wishlistId: " + WISHLIST_ID);

        verify(validationService).validatePositiveIdOrThrow(WISHLIST_ID);
        verify(wishlistRepository).findByIdWithItems(WISHLIST_ID);
    }

    @Test
    @DisplayName("Должен вернуть wishlist без элементов, когда элементы отсутствуют")
    void shouldReturnWishlistWithoutItemsWhenItemsEmpty() {
        // Given
        wishlist.setWishlistItems(List.of());
        when(wishlistRepository.findByIdWithItems(WISHLIST_ID)).thenReturn(Optional.of(wishlist));

        // When
        WishlistGetResponseDto result = wishlistGetService.getWishlist(WISHLIST_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.wishlistId()).isEqualTo(WISHLIST_ID);
        assertThat(result.items()).isEmpty();
        assertThat(result.itemsCount()).isEqualTo(0);

        verify(validationService).validatePositiveIdOrThrow(WISHLIST_ID);
        verify(wishlistRepository).findByIdWithItems(WISHLIST_ID);
    }

    @Test
    @DisplayName("Должен вернуть wishlist без элементов, когда wishlistItems равен null")
    void shouldReturnWishlistWithoutItemsWhenItemsNull() {
        // Given
        wishlist.setWishlistItems(null);
        when(wishlistRepository.findByIdWithItems(WISHLIST_ID)).thenReturn(Optional.of(wishlist));

        // When
        WishlistGetResponseDto result = wishlistGetService.getWishlist(WISHLIST_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.wishlistId()).isEqualTo(WISHLIST_ID);
        assertThat(result.items()).isEmpty();
        assertThat(result.itemsCount()).isEqualTo(0);

        verify(validationService).validatePositiveIdOrThrow(WISHLIST_ID);
        verify(wishlistRepository).findByIdWithItems(WISHLIST_ID);
    }

    @Test
    @DisplayName("Должен выбросить исключение при некорректном wishlistId")
    void shouldThrowExceptionForInvalidWishlistId() {
        // Given
        doThrow(new IllegalArgumentException("Invalid wishlistId"))
                .when(validationService).validatePositiveIdOrThrow(INVALID_WISHLIST_ID);

        // When & Then
        assertThatThrownBy(() -> wishlistGetService.getWishlist(INVALID_WISHLIST_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid wishlistId");

        verify(validationService).validatePositiveIdOrThrow(INVALID_WISHLIST_ID);
        verify(wishlistRepository, never()).findByIdWithItems(any());
    }

    @Test
    @DisplayName("Должен выбросить исключение при null wishlistId")
    void shouldThrowExceptionForNullWishlistId() {
        // Given
        doThrow(new IllegalArgumentException("WishlistId cannot be null"))
                .when(validationService).validatePositiveIdOrThrow(null);

        // When & Then
        assertThatThrownBy(() -> wishlistGetService.getWishlist(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WishlistId cannot be null");

        verify(validationService).validatePositiveIdOrThrow(null);
        verify(wishlistRepository, never()).findByIdWithItems(any());
    }

    @Test
    @DisplayName("Должен корректно обработать единичный элемент в wishlist")
    void shouldHandleSingleItemInWishlist() {
        // Given
        List<WishlistItem> wishlistItems = List.of(wishlistItem1);
        wishlist.setWishlistItems(wishlistItems);
        
        when(wishlistRepository.findByIdWithItems(WISHLIST_ID)).thenReturn(Optional.of(wishlist));

        // When
        WishlistGetResponseDto result = wishlistGetService.getWishlist(WISHLIST_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.wishlistId()).isEqualTo(WISHLIST_ID);
        assertThat(result.items()).hasSize(1);
        assertThat(result.itemsCount()).isEqualTo(1);
        
        WishlistItemGetResponseDto item = result.items().get(0);
        assertThat(item.id()).isEqualTo(1L);
        assertThat(item.productId()).isEqualTo(10L);
        assertThat(item.variantId()).isEqualTo(20L);

        verify(validationService).validatePositiveIdOrThrow(WISHLIST_ID);
        verify(wishlistRepository).findByIdWithItems(WISHLIST_ID);
    }

    @Test
    @DisplayName("Должен правильно работать с различными wishlist")
    void shouldWorkWithDifferentWishlists() {
        // Given
        Long anotherWishlistId = 200L;
        Long anotherUserId = 2L;
        
        Wishlist anotherWishlist = new Wishlist();
        anotherWishlist.setId(anotherWishlistId);
        anotherWishlist.setUserId(anotherUserId);
        anotherWishlist.setWishlistItems(List.of());
        
        when(wishlistRepository.findByIdWithItems(anotherWishlistId)).thenReturn(Optional.of(anotherWishlist));

        // When
        WishlistGetResponseDto result = wishlistGetService.getWishlist(anotherWishlistId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.wishlistId()).isEqualTo(anotherWishlistId);
        assertThat(result.items()).isEmpty();
        assertThat(result.itemsCount()).isEqualTo(0);

        verify(validationService).validatePositiveIdOrThrow(anotherWishlistId);
        verify(wishlistRepository).findByIdWithItems(anotherWishlistId);
    }

    @Test
    @DisplayName("Должен вызвать методы в правильном порядке")
    void shouldCallMethodsInCorrectOrder() {
        // Given
        wishlist.setWishlistItems(List.of(wishlistItem1));
        when(wishlistRepository.findByIdWithItems(WISHLIST_ID)).thenReturn(Optional.of(wishlist));

        // When
        wishlistGetService.getWishlist(WISHLIST_ID);

        // Then
        var inOrder = inOrder(validationService, wishlistRepository);
        inOrder.verify(validationService).validatePositiveIdOrThrow(WISHLIST_ID);
        inOrder.verify(wishlistRepository).findByIdWithItems(WISHLIST_ID);
    }

    @Test
    @DisplayName("Должен правильно маппить все поля WishlistItem")
    void shouldCorrectlyMapAllWishlistItemFields() {
        // Given
        LocalDateTime specificTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
        WishlistItem specificItem = new WishlistItem();
        specificItem.setId(999L);
        specificItem.setProductId(123L);
        specificItem.setVariantId(456L);
        specificItem.setAddedAt(specificTime);
        specificItem.setWishlist(wishlist);
        
        wishlist.setWishlistItems(List.of(specificItem));
        when(wishlistRepository.findByIdWithItems(WISHLIST_ID)).thenReturn(Optional.of(wishlist));

        // When
        WishlistGetResponseDto result = wishlistGetService.getWishlist(WISHLIST_ID);

        // Then
        assertThat(result.items()).hasSize(1);
        WishlistItemGetResponseDto item = result.items().get(0);
        
        assertThat(item.id()).isEqualTo(999L);
        assertThat(item.productId()).isEqualTo(123L);
        assertThat(item.variantId()).isEqualTo(456L);
        assertThat(item.addedAt()).isEqualTo(specificTime);
    }
}