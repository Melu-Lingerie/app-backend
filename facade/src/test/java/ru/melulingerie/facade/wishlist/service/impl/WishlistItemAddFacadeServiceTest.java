package ru.melulingerie.facade.wishlist.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionTemplate;
import ru.melulingerie.dto.WishlistAddItemRequestDto;
import ru.melulingerie.dto.WishlistAddItemResponseDto;
import ru.melulingerie.facade.wishlist.dto.WishlistAddFacadeRequestDto;
import ru.melulingerie.facade.wishlist.dto.WishlistAddFacadeResponseDto;
import ru.melulingerie.facade.wishlist.mapper.WishlistMapper;
import ru.melulingerie.facade.wishlist.mocks.ProductService;
import ru.melulingerie.facade.wishlist.mocks.entity.Product;
import ru.melulingerie.facade.wishlist.mocks.entity.ProductVariant;
import ru.melulingerie.service.WishlistAddItemService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WishlistItemAddFacadeServiceImpl Tests")
class WishlistItemAddFacadeServiceTest {

    @Mock
    private WishlistAddItemService wishlistAddItemService;

    @Mock
    private WishlistMapper wishlistMapper;

    @Mock
    private ProductService productService;

    @Mock
    private TransactionTemplate transactionTemplate;

    @InjectMocks
    private WishlistAddItemFacadeServiceImpl wishlistItemAddFacadeService;

    private static final Long WISHLIST_ID = 1L;
    private static final Long PRODUCT_ID = 10L;
    private static final Long VARIANT_ID = 20L;
    private static final Long ITEM_ID = 1L;
    private static final Long INVALID_PRODUCT_ID = 888L;
    private static final Long INVALID_VARIANT_ID = 777L;

    private Product product;
    private ProductVariant productVariant;
    private WishlistAddFacadeRequestDto facadeRequest;
    private WishlistAddItemRequestDto domainRequest;
    private WishlistAddItemResponseDto domainResponse;
    private WishlistAddFacadeResponseDto facadeResponse;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(PRODUCT_ID);
        product.setName("Test Product");

        productVariant = new ProductVariant();
        productVariant.setId(VARIANT_ID);
        productVariant.setName("Test Variant");

        facadeRequest = new WishlistAddFacadeRequestDto(PRODUCT_ID, VARIANT_ID);
        domainRequest = new WishlistAddItemRequestDto(PRODUCT_ID, VARIANT_ID);
        domainResponse = new WishlistAddItemResponseDto(ITEM_ID, "Added to wishlist");
        facadeResponse = new WishlistAddFacadeResponseDto(ITEM_ID, "Added to wishlist");
    }

    @Test
    @DisplayName("Должен успешно добавить элемент в wishlist")
    void shouldSuccessfullyAddItemToWishlist() {
        // Given
        when(productService.getProductById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productService.getProductVariantById(VARIANT_ID)).thenReturn(Optional.of(productVariant));
        when(wishlistMapper.toModuleRequestDto(facadeRequest)).thenReturn(domainRequest);
        when(wishlistMapper.toFacadeResponseDto(domainResponse)).thenReturn(facadeResponse);
        when(transactionTemplate.execute(any())).thenReturn(domainResponse);

        // When
        WishlistAddFacadeResponseDto result = wishlistItemAddFacadeService.addItemToWishlist(WISHLIST_ID, facadeRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(facadeResponse);

        verify(productService).getProductById(PRODUCT_ID);
        verify(productService).getProductVariantById(VARIANT_ID);
        verify(wishlistMapper).toModuleRequestDto(facadeRequest);
        verify(wishlistMapper).toFacadeResponseDto(domainResponse);
        verify(transactionTemplate).execute(any());
    }

    @Test
    @DisplayName("Должен выбросить исключение, если продукт не найден")
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        when(productService.getProductById(INVALID_PRODUCT_ID)).thenReturn(Optional.empty());

        WishlistAddFacadeRequestDto requestWithInvalidProduct = new WishlistAddFacadeRequestDto(INVALID_PRODUCT_ID, VARIANT_ID);

        // When & Then
        assertThatThrownBy(() -> wishlistItemAddFacadeService.addItemToWishlist(WISHLIST_ID, requestWithInvalidProduct))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product not found with id: " + INVALID_PRODUCT_ID);

        verify(productService).getProductById(INVALID_PRODUCT_ID);
        verify(productService, never()).getProductVariantById(any());
        verify(wishlistMapper, never()).toModuleRequestDto(any());
        verify(wishlistAddItemService, never()).addWishlistItem(any(), any());
        verify(wishlistMapper, never()).toFacadeResponseDto(any());
    }

    @Test
    @DisplayName("Должен выбросить исключение, если вариант продукта не найден")
    void shouldThrowExceptionWhenProductVariantNotFound() {
        // Given
        when(productService.getProductById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productService.getProductVariantById(INVALID_VARIANT_ID)).thenReturn(Optional.empty());

        WishlistAddFacadeRequestDto requestWithInvalidVariant = new WishlistAddFacadeRequestDto(PRODUCT_ID, INVALID_VARIANT_ID);

        // When & Then
        assertThatThrownBy(() -> wishlistItemAddFacadeService.addItemToWishlist(WISHLIST_ID, requestWithInvalidVariant))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product variant not found with id: " + INVALID_VARIANT_ID);

        verify(productService).getProductById(PRODUCT_ID);
        verify(productService).getProductVariantById(INVALID_VARIANT_ID);
        verify(wishlistMapper, never()).toModuleRequestDto(any());
        verify(wishlistAddItemService, never()).addWishlistItem(any(), any());
        verify(wishlistMapper, never()).toFacadeResponseDto(any());
    }

    @Test
    @DisplayName("Должен корректно передавать исключения из доменного сервиса")
    void shouldPropagateExceptionsFromDomainService() {
        // Given
        RuntimeException domainException = new RuntimeException("Domain service error");

        when(productService.getProductById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productService.getProductVariantById(VARIANT_ID)).thenReturn(Optional.of(productVariant));
        when(wishlistMapper.toModuleRequestDto(facadeRequest)).thenReturn(domainRequest);
        when(transactionTemplate.execute(any())).thenThrow(domainException);

        // When & Then
        assertThatThrownBy(() -> wishlistItemAddFacadeService.addItemToWishlist(WISHLIST_ID, facadeRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Domain service error");

        verify(productService).getProductById(PRODUCT_ID);
        verify(productService).getProductVariantById(VARIANT_ID);
        verify(wishlistMapper).toModuleRequestDto(facadeRequest);
        verify(wishlistMapper, never()).toFacadeResponseDto(any());
    }

    @Test
    @DisplayName("Должен корректно передавать исключения из маппера")
    void shouldPropagateExceptionsFromMapper() {
        // Given
        RuntimeException mapperException = new RuntimeException("Mapper error");

        when(productService.getProductById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productService.getProductVariantById(VARIANT_ID)).thenReturn(Optional.of(productVariant));
        when(wishlistMapper.toModuleRequestDto(facadeRequest)).thenThrow(mapperException);

        // When & Then
        assertThatThrownBy(() -> wishlistItemAddFacadeService.addItemToWishlist(WISHLIST_ID, facadeRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Mapper error");

        verify(productService).getProductById(PRODUCT_ID);
        verify(productService).getProductVariantById(VARIANT_ID);
        verify(wishlistMapper).toModuleRequestDto(facadeRequest);
        verify(wishlistAddItemService, never()).addWishlistItem(any(), any());
    }

    @Test
    @DisplayName("Должен правильно работать с различными продуктами и вариантами")
    void shouldWorkWithDifferentProductsAndVariants() {
        // Given
        Long anotherProductId = 30L;
        Long anotherVariantId = 40L;

        Product anotherProduct = new Product();
        anotherProduct.setId(anotherProductId);

        ProductVariant anotherVariant = new ProductVariant();
        anotherVariant.setId(anotherVariantId);

        WishlistAddFacadeRequestDto anotherRequest = new WishlistAddFacadeRequestDto(anotherProductId, anotherVariantId);
        WishlistAddItemRequestDto anotherDomainRequest = new WishlistAddItemRequestDto(anotherProductId, anotherVariantId);
        WishlistAddItemResponseDto anotherDomainResponse = new WishlistAddItemResponseDto(2L, "Added to wishlist");
        WishlistAddFacadeResponseDto anotherFacadeResponse = new WishlistAddFacadeResponseDto(2L, "Added to wishlist");

        when(productService.getProductById(anotherProductId)).thenReturn(Optional.of(anotherProduct));
        when(productService.getProductVariantById(anotherVariantId)).thenReturn(Optional.of(anotherVariant));
        when(wishlistMapper.toModuleRequestDto(anotherRequest)).thenReturn(anotherDomainRequest);
        when(wishlistMapper.toFacadeResponseDto(anotherDomainResponse)).thenReturn(anotherFacadeResponse);
        when(transactionTemplate.execute(any())).thenReturn(anotherDomainResponse);

        // When
        WishlistAddFacadeResponseDto result = wishlistItemAddFacadeService.addItemToWishlist(WISHLIST_ID, anotherRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(anotherFacadeResponse);
        assertThat(result.wishlistItemId()).isEqualTo(2L);

        verify(productService).getProductById(anotherProductId);
        verify(productService).getProductVariantById(anotherVariantId);
    }
}