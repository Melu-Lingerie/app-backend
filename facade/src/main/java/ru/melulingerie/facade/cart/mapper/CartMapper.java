package ru.melulingerie.facade.cart.mapper;

import org.mapstruct.Mapper;
import ru.melulingerie.cart.dto.request.CartAddItemRequestDto;
import ru.melulingerie.cart.dto.response.CartAddItemResponseDto;
import ru.melulingerie.cart.dto.response.CartCreateResponseDto;
import ru.melulingerie.cart.dto.request.CartUpdateQuantityRequestDto;
import ru.melulingerie.facade.cart.dto.CartAddFacadeRequestDto;
import ru.melulingerie.facade.cart.dto.CartAddFacadeResponseDto;
import ru.melulingerie.facade.cart.dto.CartCreateFacadeResponseDto;
import ru.melulingerie.facade.cart.dto.CartUpdateQuantityFacadeRequestDto;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartAddItemRequestDto toAddItemRequestDto(CartAddFacadeRequestDto facadeRequest);

    CartAddFacadeResponseDto toFacadeResponseDto(CartAddItemResponseDto domainResponse);


    CartUpdateQuantityRequestDto toUpdateQuantityRequestDto(CartUpdateQuantityFacadeRequestDto facadeRequest);
    
    CartCreateFacadeResponseDto toCreateFacadeResponseDto(CartCreateResponseDto domainResponse);
}