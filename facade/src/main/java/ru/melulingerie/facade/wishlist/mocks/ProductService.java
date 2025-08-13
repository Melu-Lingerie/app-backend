package ru.melulingerie.facade.wishlist.mocks;

import ru.melulingerie.facade.wishlist.mocks.entity.Product;
import ru.melulingerie.facade.wishlist.mocks.entity.ProductVariant;

import java.util.Optional;

public interface ProductService {
    Optional<Product> getProductById(Long id);
    Optional<ProductVariant> getProductVariantById(Long id);
}