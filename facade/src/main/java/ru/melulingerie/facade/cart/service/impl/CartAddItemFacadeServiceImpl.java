package ru.melulingerie.facade.cart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.cart.dto.request.CartAddItemRequestDto;
import ru.melulingerie.cart.dto.response.CartAddItemResponseDto;
import ru.melulingerie.facade.cart.dto.CartAddFacadeRequestDto;
import ru.melulingerie.facade.cart.dto.CartAddFacadeResponseDto;
import ru.melulingerie.facade.cart.mapper.CartMapper;
import ru.melulingerie.facade.cart.service.CartAddItemFacadeService;
import ru.melulingerie.cart.service.CartAddItemService;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartAddItemFacadeServiceImpl implements CartAddItemFacadeService {

    private final CartMapper cartMapper;
    private final CartAddItemService cartAddItemService;

    @Override
    public CartAddFacadeResponseDto addItemToCart(Long cartId, CartAddFacadeRequestDto request) {
        log.debug("Adding item to cart: cartId={}, productId={}, variantId={}", 
                  cartId, request.productId(), request.variantId());
        
        CartAddItemRequestDto domainRequest = cartMapper.toAddItemRequestDto(request);
        
        CartAddItemResponseDto response = cartAddItemService.addCartItem(cartId, domainRequest);
        
        return cartMapper.toFacadeResponseDto(response);
    }
}