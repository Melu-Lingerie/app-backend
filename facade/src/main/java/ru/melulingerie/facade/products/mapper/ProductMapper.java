package ru.melulingerie.facade.products.mapper;

import org.mapstruct.Mapper;
import ru.melulingerie.facade.config.MapStructConfig;
import ru.melulingerie.facade.products.dto.request.ProductCatalogRequestDto;
import ru.melulingerie.query.dto.request.ProductCatalogFilterRequestDto;

@Mapper(config = MapStructConfig.class)
public interface ProductMapper {

    ProductCatalogFilterRequestDto toProductCatalogFilterRequestDto(ProductCatalogRequestDto productCatalogRequestDto);

}
