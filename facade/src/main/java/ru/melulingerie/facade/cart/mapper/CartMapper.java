package ru.melulingerie.facade.cart.mapper;

import org.mapstruct.Mapper;
import ru.melulingerie.cart.dto.request.CartAddItemRequestDto;
import ru.melulingerie.cart.dto.response.CartCreateResponseDto;
import ru.melulingerie.facade.cart.dto.request.CartAddFacadeRequestDto;
import ru.melulingerie.facade.cart.dto.response.CartCreateFacadeResponseDto;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartAddItemRequestDto toAddItemRequestDto(CartAddFacadeRequestDto facadeRequest);

    CartCreateFacadeResponseDto toCreateFacadeResponseDto(CartCreateResponseDto domainResponse);
}