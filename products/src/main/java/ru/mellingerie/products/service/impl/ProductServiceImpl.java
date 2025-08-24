package ru.mellingerie.products.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.mellingerie.products.domain.Product;
import ru.mellingerie.products.repository.ProductRepository;
import ru.mellingerie.products.service.ProductService;
import ru.mellingerie.products.dto.request.ProductFilterRequestDto;
import ru.mellingerie.products.dto.response.ProductItemResponseDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Page<ProductItemResponseDto> getPageOfProducts(ProductFilterRequestDto productFilterRequestDto) {
        Page<Product> productsByParams = productRepository.findByParams(productFilterRequestDto);

        return productsByParams.map(entity -> new ProductItemResponseDto(
                entity.getId(),
                entity.getBasePrice(),
                entity.getName()
        ));
    }
}
