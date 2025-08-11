package ru.melulingerie.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.melulingerie.exception.AggregatedValidationException;
import ru.melulingerie.util.WishlistValidator;
import ru.melulingerie.wishlist.domain.Wishlist;
import ru.melulingerie.wishlist.repository.WishlistItemRepository;
import ru.melulingerie.wishlist.repository.WishlistRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WishlistClearServiceImpl Tests")
class WishlistClearServiceImplTest {

    @Mock
    private WishlistRepository wishlistRepository;
    
    @Mock
    private WishlistValidator validationService;
    
    @Mock
    private WishlistItemRepository wishlistItemRepository;
    
    @InjectMocks
    private WishlistClearServiceImpl wishlistClearService;

    private static final Long WISHLIST_ID = 100L;
    private static final Long USER_ID = 1L;
    private static final Long INVALID_WISHLIST_ID = -1L;

    private Wishlist wishlist;

    @BeforeEach
    void setUp() {
        wishlist = new Wishlist();
        wishlist.setId(WISHLIST_ID);
        wishlist.setUserId(USER_ID);
    }

    @Test
    @DisplayName("Должен успешно очистить wishlist")
    void shouldSuccessfullyClearWishlist() {
        // Given
        int expectedDeletedCount = 5;
        when(wishlistRepository.findById(WISHLIST_ID)).thenReturn(Optional.of(wishlist));
        when(wishlistItemRepository.deleteAllByWishlistId(WISHLIST_ID)).thenReturn(expectedDeletedCount);

        // When
        int result = wishlistClearService.clearWishlist(WISHLIST_ID);

        // Then
        assertThat(result).isEqualTo(expectedDeletedCount);
        verify(validationService).validatePositiveIdOrThrow(WISHLIST_ID);
        verify(wishlistRepository).findById(WISHLIST_ID);
        verify(validationService).validateClearRequest(wishlist.getUserId(), wishlist);
        verify(wishlistItemRepository).deleteAllByWishlistId(WISHLIST_ID);
    }

    @Test
    @DisplayName("Должен вернуть 0 если wishlist не найден")
    void shouldReturnZeroWhenWishlistNotFound() {
        // Given
        when(wishlistRepository.findById(WISHLIST_ID)).thenReturn(Optional.empty());

        // When
        int result = wishlistClearService.clearWishlist(WISHLIST_ID);

        // Then
        assertThat(result).isEqualTo(0);
        verify(validationService).validatePositiveIdOrThrow(WISHLIST_ID);
        verify(wishlistRepository).findById(WISHLIST_ID);
        verify(validationService, never()).validateClearRequest(any(), any());
        verify(wishlistItemRepository, never()).deleteAllByWishlistId(any());
    }

    @Test
    @DisplayName("Должен выбросить исключение для некорректного wishlistId")
    void shouldThrowExceptionForInvalidWishlistId() {
        // Given
        doThrow(new IllegalArgumentException("Invalid wishlistId"))
                .when(validationService).validatePositiveIdOrThrow(INVALID_WISHLIST_ID);

        // When & Then
        assertThatThrownBy(() -> wishlistClearService.clearWishlist(INVALID_WISHLIST_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid wishlistId");

        verify(validationService).validatePositiveIdOrThrow(INVALID_WISHLIST_ID);
        verify(wishlistRepository, never()).findById(any());
        verify(wishlistItemRepository, never()).deleteAllByWishlistId(any());
    }

    @Test
    @DisplayName("Должен выбросить исключение для null wishlistId")
    void shouldThrowExceptionForNullWishlistId() {
        // Given
        doThrow(new IllegalArgumentException("WishlistId cannot be null"))
                .when(validationService).validatePositiveIdOrThrow(null);

        // When & Then
        assertThatThrownBy(() -> wishlistClearService.clearWishlist(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WishlistId cannot be null");

        verify(validationService).validatePositiveIdOrThrow(null);
        verify(wishlistRepository, never()).findById(any());
        verify(wishlistItemRepository, never()).deleteAllByWishlistId(any());
    }

    @Test
    @DisplayName("Должен выбросить исключение при ошибке валидации")
    void shouldThrowExceptionOnValidationError() {
        // Given
        when(wishlistRepository.findById(WISHLIST_ID)).thenReturn(Optional.of(wishlist));
        
        doThrow(new AggregatedValidationException(List.of("Validation error")))
                .when(validationService).validateClearRequest(USER_ID, wishlist);

        // When & Then
        assertThatThrownBy(() -> wishlistClearService.clearWishlist(WISHLIST_ID))
                .isInstanceOf(AggregatedValidationException.class);

        verify(validationService).validatePositiveIdOrThrow(WISHLIST_ID);
        verify(wishlistRepository).findById(WISHLIST_ID);
        verify(validationService).validateClearRequest(wishlist.getUserId(), wishlist);
        verify(wishlistItemRepository, never()).deleteAllByWishlistId(any());
    }

    @Test
    @DisplayName("Должен вернуть 0 для пустого wishlist")
    void shouldReturnZeroForEmptyWishlist() {
        // Given
        when(wishlistRepository.findById(WISHLIST_ID)).thenReturn(Optional.of(wishlist));
        when(wishlistItemRepository.deleteAllByWishlistId(WISHLIST_ID)).thenReturn(0);

        // When
        int result = wishlistClearService.clearWishlist(WISHLIST_ID);

        // Then
        assertThat(result).isEqualTo(0);
        verify(validationService).validatePositiveIdOrThrow(WISHLIST_ID);
        verify(wishlistRepository).findById(WISHLIST_ID);
        verify(validationService).validateClearRequest(wishlist.getUserId(), wishlist);
        verify(wishlistItemRepository).deleteAllByWishlistId(WISHLIST_ID);
    }

    @Test
    @DisplayName("Должен обработать wishlist с большим количеством элементов")
    void shouldHandleWishlistWithManyItems() {
        // Given
        int largeDeletedCount = 1000;
        when(wishlistRepository.findById(WISHLIST_ID)).thenReturn(Optional.of(wishlist));
        when(wishlistItemRepository.deleteAllByWishlistId(WISHLIST_ID)).thenReturn(largeDeletedCount);

        // When
        int result = wishlistClearService.clearWishlist(WISHLIST_ID);

        // Then
        assertThat(result).isEqualTo(largeDeletedCount);
        verify(validationService).validatePositiveIdOrThrow(WISHLIST_ID);
        verify(wishlistRepository).findById(WISHLIST_ID);
        verify(validationService).validateClearRequest(wishlist.getUserId(), wishlist);
        verify(wishlistItemRepository).deleteAllByWishlistId(WISHLIST_ID);
    }

    @Test
    @DisplayName("Должен правильно работать с различными wishlist")
    void shouldWorkWithDifferentWishlists() {
        // Given
        Long anotherWishlistId = 200L;
        Long anotherUserId = 2L;
        int expectedDeletedCount = 3;
        
        Wishlist anotherWishlist = new Wishlist();
        anotherWishlist.setId(anotherWishlistId);
        anotherWishlist.setUserId(anotherUserId);
        
        when(wishlistRepository.findById(anotherWishlistId)).thenReturn(Optional.of(anotherWishlist));
        when(wishlistItemRepository.deleteAllByWishlistId(anotherWishlistId)).thenReturn(expectedDeletedCount);

        // When
        int result = wishlistClearService.clearWishlist(anotherWishlistId);

        // Then
        assertThat(result).isEqualTo(expectedDeletedCount);
        verify(validationService).validatePositiveIdOrThrow(anotherWishlistId);
        verify(wishlistRepository).findById(anotherWishlistId);
        verify(validationService).validateClearRequest(anotherUserId, anotherWishlist);
        verify(wishlistItemRepository).deleteAllByWishlistId(anotherWishlistId);
    }

    @Test
    @DisplayName("Должен вызвать методы в правильном порядке")
    void shouldCallMethodsInCorrectOrder() {
        // Given
        int expectedDeletedCount = 2;
        when(wishlistRepository.findById(WISHLIST_ID)).thenReturn(Optional.of(wishlist));
        when(wishlistItemRepository.deleteAllByWishlistId(WISHLIST_ID)).thenReturn(expectedDeletedCount);

        // When
        int result = wishlistClearService.clearWishlist(WISHLIST_ID);

        // Then
        assertThat(result).isEqualTo(expectedDeletedCount);
        
        // Verify order of method calls
        var inOrder = inOrder(validationService, wishlistRepository, wishlistItemRepository);
        inOrder.verify(validationService).validatePositiveIdOrThrow(WISHLIST_ID);
        inOrder.verify(wishlistRepository).findById(WISHLIST_ID);
        inOrder.verify(validationService).validateClearRequest(wishlist.getUserId(), wishlist);
        inOrder.verify(wishlistItemRepository).deleteAllByWishlistId(WISHLIST_ID);
    }
}