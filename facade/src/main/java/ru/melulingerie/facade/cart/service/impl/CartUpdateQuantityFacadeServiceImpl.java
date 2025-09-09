package ru.melulingerie.facade.cart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.cart.dto.request.CartUpdateQuantityRequestDto;
import ru.melulingerie.cart.service.CartUpdateQuantityService;
import ru.melulingerie.facade.cart.dto.CartUpdateQuantityFacadeRequestDto;
import ru.melulingerie.facade.cart.mapper.CartMapper;
import ru.melulingerie.facade.cart.service.CartUpdateQuantityFacadeService;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartUpdateQuantityFacadeServiceImpl implements CartUpdateQuantityFacadeService {

    private final CartMapper cartMapper;
    private final CartUpdateQuantityService cartUpdateQuantityService;

    @Override
    public void updateItemQuantity(Long cartId, Long itemId, CartUpdateQuantityFacadeRequestDto request) {
        log.debug("Updating quantity for cartId: {}, itemId: {}, quantity: {}", cartId, itemId, request.quantity());

        CartUpdateQuantityRequestDto domainRequest = cartMapper.toUpdateQuantityRequestDto(request);

        cartUpdateQuantityService.updateItemQuantity(cartId, itemId, domainRequest);
    }
}