package ru.melulingerie.facade.cart.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.melulingerie.cart.dto.request.CartAddItemRequestDto;
import ru.melulingerie.cart.dto.response.CartAddItemResponseDto;
import ru.melulingerie.cart.dto.response.CartGetResponseDto;
import ru.melulingerie.cart.dto.response.CartItemGetResponseDto;
import ru.melulingerie.cart.service.CartAddItemService;
import ru.melulingerie.cart.service.CartGetService;
import ru.melulingerie.facade.cart.dto.request.CartAddFacadeRequestDto;
import ru.melulingerie.facade.cart.dto.response.CartAddFacadeResponseDto;
import ru.melulingerie.facade.cart.dto.CartOperationType;
import ru.melulingerie.facade.cart.mapper.CartMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartAddItemFacadeService Unit Tests")
class CartAddItemFacadeServiceImplTest {

    @Mock
    private CartMapper cartMapper;
    
    @Mock
    private CartAddItemService cartAddItemService;
    
    @Mock
    private CartGetService cartGetService;
    
    @InjectMocks
    private CartAddItemFacadeServiceImpl cartAddItemFacadeService;

    @Test
    @DisplayName("Should add new item and return enriched response")
    void shouldAddNewItemAndReturnEnrichedResponse() {
        // Given
        Long cartId = 1L;
        Long productId = 100L;
        Long variantId = 200L;
        Integer quantity = 3;
        Long cartItemId = 10L;
        
        CartAddFacadeRequestDto facadeRequest = new CartAddFacadeRequestDto(productId, variantId, quantity);
        CartAddItemRequestDto domainRequest = new CartAddItemRequestDto(productId, variantId, quantity);
        CartAddItemResponseDto domainResponse = new CartAddItemResponseDto(cartItemId, quantity, "Added to cart");
        
        // Mock cart state after addition
        CartItemGetResponseDto cartItem = new CartItemGetResponseDto(
                cartItemId, productId, variantId, quantity, LocalDateTime.now()
        );
        CartGetResponseDto cartGetResponse = new CartGetResponseDto(cartId, List.of(cartItem), 1);

        when(cartMapper.toAddItemRequestDto(facadeRequest)).thenReturn(domainRequest);
        when(cartAddItemService.addCartItem(cartId, domainRequest)).thenReturn(domainResponse);
        when(cartGetService.getCart(cartId)).thenReturn(cartGetResponse);

        // When
        CartAddFacadeResponseDto result = cartAddItemFacadeService.addItemToCart(cartId, facadeRequest);

        // Then
        assertNotNull(result);
        assertEquals(cartItemId, result.cartItemId());
        assertEquals(quantity, result.finalQuantity());
        assertEquals(CartOperationType.ITEM_ADDED, result.operation());
        
        // Verify item total price calculation (mock price: 100 + productId*10 + variantId = 100 + 1000 + 200 = 1300)
        BigDecimal expectedItemPrice = BigDecimal.valueOf(1300).multiply(BigDecimal.valueOf(quantity)); // 1300 * 3 = 3900
        assertEquals(0, expectedItemPrice.compareTo(result.itemTotalPrice()));
        
        // Verify cart totals
        assertNotNull(result.cartTotals());
        assertEquals(0, expectedItemPrice.compareTo(result.cartTotals().totalAmount())); // Same as item total since it's the only item
        assertEquals(1, result.cartTotals().totalItemsCount());

        // Verify interactions
        verify(cartMapper).toAddItemRequestDto(facadeRequest);
        verify(cartAddItemService).addCartItem(cartId, domainRequest);
        verify(cartGetService).getCart(cartId);
    }

    @Test
    @DisplayName("Should update quantity and return correct operation type")
    void shouldUpdateQuantityAndReturnCorrectOperationType() {
        // Given
        Long cartId = 1L;
        Long productId = 100L;
        Long variantId = 200L;
        Integer requestQuantity = 2;
        Integer finalQuantity = 5; // Existing 3 + new 2
        Long cartItemId = 10L;
        
        CartAddFacadeRequestDto facadeRequest = new CartAddFacadeRequestDto(productId, variantId, requestQuantity);
        CartAddItemRequestDto domainRequest = new CartAddItemRequestDto(productId, variantId, requestQuantity);
        CartAddItemResponseDto domainResponse = new CartAddItemResponseDto(cartItemId, finalQuantity, "Quantity updated in cart");
        
        CartItemGetResponseDto cartItem = new CartItemGetResponseDto(
                cartItemId, productId, variantId, finalQuantity, LocalDateTime.now()
        );
        CartGetResponseDto cartGetResponse = new CartGetResponseDto(cartId, List.of(cartItem), 1);

        when(cartMapper.toAddItemRequestDto(facadeRequest)).thenReturn(domainRequest);
        when(cartAddItemService.addCartItem(cartId, domainRequest)).thenReturn(domainResponse);
        when(cartGetService.getCart(cartId)).thenReturn(cartGetResponse);

        // When
        CartAddFacadeResponseDto result = cartAddItemFacadeService.addItemToCart(cartId, facadeRequest);

        // Then
        assertNotNull(result);
        assertEquals(cartItemId, result.cartItemId());
        assertEquals(finalQuantity, result.finalQuantity());
        assertEquals(CartOperationType.QUANTITY_INCREASED, result.operation());
        
        // Verify item total price calculation for final quantity
        BigDecimal expectedUnitPrice = BigDecimal.valueOf(100 + (productId * 10) + variantId); // 1300
        BigDecimal expectedItemPrice = expectedUnitPrice.multiply(BigDecimal.valueOf(finalQuantity)); // 1300 * 5 = 6500
        assertEquals(0, expectedItemPrice.compareTo(result.itemTotalPrice()));
    }
}