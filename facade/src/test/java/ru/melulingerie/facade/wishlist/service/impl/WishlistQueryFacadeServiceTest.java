package ru.melulingerie.facade.wishlist.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.melulingerie.dto.WishlistItemGetResponseDto;
import ru.melulingerie.dto.WishlistGetResponseDto;
import ru.melulingerie.facade.wishlist.dto.WishlistGetFacadeResponseDto;
import ru.melulingerie.facade.wishlist.mapper.WishlistMapper;
import ru.melulingerie.service.WishlistGetService;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WishlistQueryFacadeServiceImpl Tests")
class WishlistQueryFacadeServiceTest {

    @Mock
    private WishlistGetService wishlistGetService;
    
    @Mock
    private WishlistMapper wishlistMapper;
    
    @Mock
    private TransactionTemplate transactionTemplate;
    
    @InjectMocks
    private WishlistGetFacadeServiceImpl wishlistQueryFacadeService;

    private static final Long WISHLIST_ID = 1L;

    private WishlistGetResponseDto domainResponse;
    private WishlistGetFacadeResponseDto facadeResponse;

    @BeforeEach
    void setUp() {
        WishlistItemGetResponseDto item1 = new WishlistItemGetResponseDto(
                1L, 10L, 20L, LocalDateTime.now()
        );
        WishlistItemGetResponseDto item2 = new WishlistItemGetResponseDto(
                2L, 11L, 21L, LocalDateTime.now()
        );

        domainResponse = new WishlistGetResponseDto(WISHLIST_ID, List.of(item1, item2), 2);
        
        facadeResponse = new WishlistGetFacadeResponseDto(List.of(), 2);
    }

    @Test
    @DisplayName("Должен успешно получить wishlist")
    void shouldSuccessfullyGetWishlist() {
        // Given
        when(transactionTemplate.execute(any())).thenReturn(domainResponse);
        when(wishlistMapper.toFacadeWishListResponseDto(domainResponse)).thenReturn(facadeResponse);

        // When
        WishlistGetFacadeResponseDto result = wishlistQueryFacadeService.getWishlist(WISHLIST_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(facadeResponse);

        verify(transactionTemplate).execute(any());
        verify(wishlistMapper).toFacadeWishListResponseDto(domainResponse);
    }

    @Test
    @DisplayName("Должен успешно обработать пустой wishlist")
    void shouldHandleEmptyWishlist() {
        // Given
        WishlistGetResponseDto emptyDomainResponse = new WishlistGetResponseDto(WISHLIST_ID, List.of(), 0);
        WishlistGetFacadeResponseDto emptyFacadeResponse = new WishlistGetFacadeResponseDto(List.of(), 0);

        when(transactionTemplate.execute(any())).thenReturn(emptyDomainResponse);
        when(wishlistMapper.toFacadeWishListResponseDto(emptyDomainResponse)).thenReturn(emptyFacadeResponse);

        // When
        WishlistGetFacadeResponseDto result = wishlistQueryFacadeService.getWishlist(WISHLIST_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(emptyFacadeResponse);
        assertThat(result.itemsCount()).isEqualTo(0);

        verify(transactionTemplate).execute(any());
        verify(wishlistMapper).toFacadeWishListResponseDto(emptyDomainResponse);
    }

    @Test
    @DisplayName("Должен корректно передавать исключения из доменного сервиса")
    void shouldPropagateExceptionsFromDomainService() {
        // Given
        RuntimeException domainException = new RuntimeException("Domain error");
        
        when(transactionTemplate.execute(any())).thenThrow(domainException);

        // When & Then
        assertThatThrownBy(() -> wishlistQueryFacadeService.getWishlist(WISHLIST_ID))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Domain error");

        verify(transactionTemplate).execute(any());
        verify(wishlistMapper, never()).toFacadeWishListResponseDto(any());
    }

    @Test
    @DisplayName("Должен корректно передавать исключения из маппера")
    void shouldPropagateExceptionsFromMapper() {
        // Given
        RuntimeException mapperException = new RuntimeException("Mapping error");
        
        when(transactionTemplate.execute(any())).thenReturn(domainResponse);
        when(wishlistMapper.toFacadeWishListResponseDto(domainResponse)).thenThrow(mapperException);

        // When & Then
        assertThatThrownBy(() -> wishlistQueryFacadeService.getWishlist(WISHLIST_ID))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Mapping error");

        verify(transactionTemplate).execute(any());
        verify(wishlistMapper).toFacadeWishListResponseDto(domainResponse);
    }

    @Test
    @DisplayName("Должен правильно работать с разными wishlist")
    void shouldWorkWithDifferentWishlists() {
        // Given
        Long anotherWishlistId = 2L;

        WishlistGetResponseDto anotherDomainResponse = new WishlistGetResponseDto(anotherWishlistId, List.of(), 0);
        WishlistGetFacadeResponseDto anotherFacadeResponse = new WishlistGetFacadeResponseDto(List.of(), 0);

        when(transactionTemplate.execute(any())).thenReturn(anotherDomainResponse);
        when(wishlistMapper.toFacadeWishListResponseDto(anotherDomainResponse)).thenReturn(anotherFacadeResponse);

        // When
        WishlistGetFacadeResponseDto result = wishlistQueryFacadeService.getWishlist(anotherWishlistId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(anotherFacadeResponse);

        verify(transactionTemplate).execute(any());
        verify(wishlistMapper).toFacadeWishListResponseDto(anotherDomainResponse);
    }

    @Test
    @DisplayName("Должен правильно работать с единичным элементом в wishlist")
    void shouldHandleSingleItemWishlist() {
        // Given
        WishlistItemGetResponseDto singleItem = new WishlistItemGetResponseDto(
                1L, 10L, 20L, LocalDateTime.now()
        );
        WishlistGetResponseDto singleItemDomainResponse = new WishlistGetResponseDto(WISHLIST_ID, List.of(singleItem), 1);
        WishlistGetFacadeResponseDto singleItemFacadeResponse = new WishlistGetFacadeResponseDto(List.of(), 1);

        when(transactionTemplate.execute(any())).thenReturn(singleItemDomainResponse);
        when(wishlistMapper.toFacadeWishListResponseDto(singleItemDomainResponse)).thenReturn(singleItemFacadeResponse);

        // When
        WishlistGetFacadeResponseDto result = wishlistQueryFacadeService.getWishlist(WISHLIST_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.itemsCount()).isEqualTo(1);

        verify(transactionTemplate).execute(any());
        verify(wishlistMapper).toFacadeWishListResponseDto(singleItemDomainResponse);
    }

    @Test
    @DisplayName("Должен передавать параметры в правильном порядке в доменный сервис")
    void shouldCallDomainServiceWithCorrectParameters() {
        // Given
        when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            wishlistGetService.getWishlist(WISHLIST_ID);
            return domainResponse;
        });
        when(wishlistMapper.toFacadeWishListResponseDto(domainResponse)).thenReturn(facadeResponse);

        // When
        wishlistQueryFacadeService.getWishlist(WISHLIST_ID);

        // Then
        verify(transactionTemplate).execute(any());
        verify(wishlistGetService).getWishlist(WISHLIST_ID);
        verify(wishlistMapper).toFacadeWishListResponseDto(domainResponse);
    }
}