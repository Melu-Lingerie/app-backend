package ru.melulingerie.facade.cart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.cart.dto.request.CartAddItemRequestDto;
import ru.melulingerie.cart.dto.response.CartAddItemResponseDto;
import ru.melulingerie.facade.cart.dto.request.CartAddFacadeRequestDto;
import ru.melulingerie.facade.cart.dto.response.CartAddFacadeResponseDto;
import ru.melulingerie.facade.cart.dto.CartOperationType;
import ru.melulingerie.facade.cart.dto.CartTotalsDto;
import ru.melulingerie.facade.cart.mapper.CartMapper;
import ru.melulingerie.facade.cart.service.CartAddItemFacadeService;
import ru.melulingerie.cart.service.CartAddItemService;
import ru.melulingerie.cart.service.CartGetService;
import ru.melulingerie.cart.dto.response.CartGetResponseDto;
// TODO: Uncomment when price service is ready
// import ru.melulingerie.price.service.PriceService;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartAddItemFacadeServiceImpl implements CartAddItemFacadeService {

    private final CartMapper cartMapper;
    private final CartAddItemService cartAddItemService;
    private final CartGetService cartGetService;
    // TODO: Uncomment when price service is ready
    // private final PriceService priceService;

    @Override
    public CartAddFacadeResponseDto addItemToCart(Long cartId, CartAddFacadeRequestDto request) {
        log.debug("Adding item to cart: cartId={}, productId={}, variantId={}", 
                  cartId, request.productId(), request.variantId());
        
        CartAddItemRequestDto domainRequest = cartMapper.toAddItemRequestDto(request);
        
        CartAddItemResponseDto response = cartAddItemService.addCartItem(cartId, domainRequest);
        
        // Определяем тип операции
        CartOperationType operationType = determineOperationType(response.message());
        
        // Рассчитываем цену товара
        // TODO: When price service is ready, replace mock calculation:
        // BigDecimal unitPrice = priceService.getVariantPrice(request.productId(), request.variantId());
        BigDecimal unitPrice = BigDecimal.valueOf(100.00 + (request.productId() * 10) + request.variantId());
        BigDecimal itemTotalPrice = unitPrice.multiply(BigDecimal.valueOf(response.finalQuantity()));
        
        // Получаем обновленные итоги корзины
        CartTotalsDto cartTotals = calculateCartTotals(cartId);
        
        return new CartAddFacadeResponseDto(
                response.cartItemId(),
                response.finalQuantity(),
                itemTotalPrice,
                cartTotals,
                operationType
        );
    }
    
    private CartOperationType determineOperationType(String message) {
        return switch (message) {
            case "Added to cart" -> CartOperationType.ITEM_ADDED;
            case "Quantity updated in cart" -> CartOperationType.QUANTITY_INCREASED;
            default -> CartOperationType.QUANTITY_UPDATED;
        };
    }
    
    private CartTotalsDto calculateCartTotals(Long cartId) {
        CartGetResponseDto cartData = cartGetService.getCart(cartId);
        
        // Рассчитываем общую сумму с временными ценами
        BigDecimal totalAmount = cartData.items().stream()
                .map(item -> {
                    // TODO: When price service is ready, replace mock calculation:
                    // BigDecimal unitPrice = priceService.getVariantPrice(item.productId(), item.variantId());
                    BigDecimal unitPrice = BigDecimal.valueOf(100.00 + (item.productId() * 10) + item.variantId());
                    return unitPrice.multiply(BigDecimal.valueOf(item.quantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Доставка пока бесплатная
        BigDecimal deliveryAmount = BigDecimal.ZERO;
        
        return new CartTotalsDto(totalAmount, cartData.itemsCount(), deliveryAmount);
    }
}