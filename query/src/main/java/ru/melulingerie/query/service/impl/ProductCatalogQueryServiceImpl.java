package ru.melulingerie.query.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.melulingerie.query.dto.request.ProductCatalogFilterRequestDto;
import ru.melulingerie.query.dto.response.ProductCatalogItemResponseDto;
import ru.melulingerie.query.repository.ProductCatalogNativeRepository;
import ru.melulingerie.query.service.ProductCatalogQueryService;

@Service
@RequiredArgsConstructor
public class ProductCatalogQueryServiceImpl implements ProductCatalogQueryService {

    public final ProductCatalogNativeRepository repository;

    @Override
    public Page<ProductCatalogItemResponseDto> getProductCatalogItems(ProductCatalogFilterRequestDto req, Pageable pageable) {
        return repository.findCatalog(req, pageable);
    }
}
