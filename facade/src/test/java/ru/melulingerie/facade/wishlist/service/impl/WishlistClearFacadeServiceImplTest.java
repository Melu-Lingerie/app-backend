package ru.melulingerie.facade.wishlist.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionTemplate;
import ru.melulingerie.service.WishlistClearService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WishlistClearFacadeServiceImpl Tests")
class WishlistClearFacadeServiceImplTest {

    @Mock
    private WishlistClearService wishlistClearService;
    
    @Mock
    private TransactionTemplate transactionTemplate;
    
    @InjectMocks
    private WishlistClearFacadeServiceImpl wishlistClearFacadeService;

    private static final Long WISHLIST_ID = 1L;

    @Test
    @DisplayName("Должен успешно очистить wishlist")
    void shouldSuccessfullyClearWishlist() {
        // Given
        int expectedDeletedCount = 5;
        when(transactionTemplate.execute(any())).thenReturn(expectedDeletedCount);

        // When
        int result = wishlistClearFacadeService.clearWishlist(WISHLIST_ID);

        // Then
        assertThat(result).isEqualTo(expectedDeletedCount);
        verify(transactionTemplate).execute(any());
    }

    @Test
    @DisplayName("Должен вернуть 0 если wishlist пустой")
    void shouldReturnZeroWhenWishlistEmpty() {
        // Given
        when(transactionTemplate.execute(any())).thenReturn(0);

        // When
        int result = wishlistClearFacadeService.clearWishlist(WISHLIST_ID);

        // Then
        assertThat(result).isEqualTo(0);
        verify(transactionTemplate).execute(any());
    }

    @Test
    @DisplayName("Должен вернуть 0 если транзакция вернула null")
    void shouldReturnZeroWhenTransactionReturnsNull() {
        // Given
        when(transactionTemplate.execute(any())).thenReturn(null);

        // When
        int result = wishlistClearFacadeService.clearWishlist(WISHLIST_ID);

        // Then
        assertThat(result).isEqualTo(0);
        verify(transactionTemplate).execute(any());
    }

    @Test
    @DisplayName("Должен корректно передавать исключения из доменного сервиса")
    void shouldPropagateExceptionsFromDomainService() {
        // Given
        RuntimeException domainException = new RuntimeException("Domain service error");
        when(transactionTemplate.execute(any())).thenThrow(domainException);

        // When & Then
        assertThatThrownBy(() -> wishlistClearFacadeService.clearWishlist(WISHLIST_ID))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Domain service error");

        verify(transactionTemplate).execute(any());
    }

    @Test
    @DisplayName("Должен вызвать транзакцию и доменный сервис в правильном порядке")
    void shouldCallTransactionAndDomainServiceInCorrectOrder() {
        // Given
        int expectedDeletedCount = 3;
        when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            // Simulate the transaction execution
            return wishlistClearService.clearWishlist(WISHLIST_ID);
        });
        when(wishlistClearService.clearWishlist(WISHLIST_ID)).thenReturn(expectedDeletedCount);

        // When
        int result = wishlistClearFacadeService.clearWishlist(WISHLIST_ID);

        // Then
        assertThat(result).isEqualTo(expectedDeletedCount);
        verify(transactionTemplate).execute(any());
        verify(wishlistClearService).clearWishlist(WISHLIST_ID);
    }

    @Test
    @DisplayName("Должен правильно работать с различными wishlist")
    void shouldWorkWithDifferentWishlists() {
        // Given
        Long anotherWishlistId = 2L;
        int expectedDeletedCount = 2;
        when(transactionTemplate.execute(any())).thenReturn(expectedDeletedCount);

        // When
        int result = wishlistClearFacadeService.clearWishlist(anotherWishlistId);

        // Then
        assertThat(result).isEqualTo(expectedDeletedCount);
        verify(transactionTemplate).execute(any());
    }

    @Test
    @DisplayName("Должен работать с null wishlistId если доменный сервис это поддерживает")
    void shouldWorkWithNullWishlistId() {
        // Given
        when(transactionTemplate.execute(any())).thenReturn(0);

        // When
        int result = wishlistClearFacadeService.clearWishlist(null);

        // Then
        assertThat(result).isEqualTo(0);
        verify(transactionTemplate).execute(any());
    }

    @Test
    @DisplayName("Должен корректно обрабатывать большое количество удаленных элементов")
    void shouldHandleLargeNumberOfDeletedItems() {
        // Given
        int largeDeletedCount = 1000;
        when(transactionTemplate.execute(any())).thenReturn(largeDeletedCount);

        // When
        int result = wishlistClearFacadeService.clearWishlist(WISHLIST_ID);

        // Then
        assertThat(result).isEqualTo(largeDeletedCount);
        verify(transactionTemplate).execute(any());
    }
}