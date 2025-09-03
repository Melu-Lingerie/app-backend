package ru.melulingerie.facade.cart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.dto.CartGetResponseDto;
import ru.melulingerie.facade.cart.dto.CartGetFacadeResponseDto;
import ru.melulingerie.facade.cart.mapper.CartMapper;
import ru.melulingerie.facade.cart.service.CartGetFacadeService;
import ru.melulingerie.service.CartGetService;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartGetFacadeServiceImpl implements CartGetFacadeService {

    private final CartMapper cartMapper;
    private final CartGetService cartGetService;

    @Override
    public CartGetFacadeResponseDto getCart(Long cartId) {
        log.debug("Getting cart for cartId: {}", cartId);
        
        CartGetResponseDto domainResponse = cartGetService.getCart(cartId);
        
        return cartMapper.toFacadeCartResponseDto(domainResponse);
    }
}