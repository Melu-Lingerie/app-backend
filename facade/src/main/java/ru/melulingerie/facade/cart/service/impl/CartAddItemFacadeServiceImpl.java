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
import ru.melulingerie.price.service.PriceService;
import ru.melulingerie.products.service.ProductVariantService;

import java.math.BigDecimal;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartAddItemFacadeServiceImpl implements CartAddItemFacadeService {

    private final CartMapper cartMapper;
    private final PriceService priceService;
    private final CartGetService cartGetService;
    private final CartAddItemService cartAddItemService;
    private final ProductVariantService productVariantService;

    @Override
    public CartAddFacadeResponseDto addItemToCart(Long cartId, CartAddFacadeRequestDto request) {
        log.debug("Adding item to cart: cartId={}, productId={}, variantId={}", 
                  cartId, request.productId(), request.variantId());
        
        CartAddItemRequestDto domainRequest = cartMapper.toAddItemRequestDto(request);
        
        CartAddItemResponseDto response = cartAddItemService.addCartItem(cartId, domainRequest);
        
        // Определяем тип операции
        CartOperationType operationType = determineOperationType(response.message());
        
        // Рассчитываем цену товара
        Long priceId = productVariantService.getVariantById(request.variantId()).getPriceId();
        BigDecimal unitPrice = priceService.getPriceById(priceId)
                .price();
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
                    Long itemPriceId = productVariantService.getVariantById(item.variantId()).getPriceId();
                    BigDecimal itemUnitPrice = priceService.getPriceById(itemPriceId)
                            .price();
                    return itemUnitPrice.multiply(BigDecimal.valueOf(item.quantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Доставка пока бесплатная
        BigDecimal deliveryAmount = BigDecimal.ZERO;
        
        return new CartTotalsDto(totalAmount, cartData.itemsCount());
    }
}