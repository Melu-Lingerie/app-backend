package ru.melulingerie.facade.cart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.cart.dto.response.CartGetResponseDto;
import ru.melulingerie.cart.dto.response.CartItemGetResponseDto;
import ru.melulingerie.cart.service.CartGetService;
import ru.melulingerie.facade.cart.dto.response.CartGetFacadeResponseDto;
import ru.melulingerie.facade.cart.dto.response.CartItemDetailsFacadeResponseDto;
import ru.melulingerie.facade.cart.service.CartGetFacadeService;
import ru.melulingerie.media.dto.MediaGetInfoResponseDto;
import ru.melulingerie.media.service.MediaGetService;
import ru.melulingerie.price.dto.response.PriceQuoteDto;
import ru.melulingerie.price.service.PriceService;
import ru.melulingerie.products.domain.ProductVariant;
import ru.melulingerie.products.domain.ProductVariantMedia;
import ru.melulingerie.products.service.ProductService;
import ru.melulingerie.products.service.ProductVariantService;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartGetFacadeServiceImpl implements CartGetFacadeService {

    private final PriceService priceService;
    private final ProductService productService;
    private final CartGetService cartGetService;
    private final MediaGetService mediaGetService;
    private final ProductVariantService productVariantService;

    /**
     * Получает корзину с детализированной информацией о товарах, ценах и изображениях
     *
     * @param cartId идентификатор корзины
     * @return детализированная информация о корзине
     */
    @Override
    public CartGetFacadeResponseDto getCart(Long cartId) {
        log.debug("Retrieving cart details for cartId: {}", cartId);

        try {
            CartGetResponseDto cartData = cartGetService.getCart(cartId);

            List<Long> productIds = cartData.items().stream().map(CartItemGetResponseDto::productId).toList();
            Map<Long/*productId*/, Long/*categoryId*/> categoryIdByProductIds = productService.getCategoryIdByProductIds(productIds);

            List<CartItemDetailsFacadeResponseDto> enrichedItems = enrichCartItems(cartData.items(), categoryIdByProductIds);
            BigDecimal totalAmount = calculateCartTotalAmount(enrichedItems);



            log.debug("Successfully enriched cart {} with {} items, total: {}",
                    cartId, enrichedItems.size(), totalAmount);

            return new CartGetFacadeResponseDto(
                    enrichedItems,
                    cartData.itemsCount(),
                    totalAmount
            );
        } catch (Exception e) {
            log.error("Failed to retrieve cart details for cartId: {}", cartId, e);
            throw e;
        }
    }

    /**
     * Обогащает список товаров корзины полной информацией с использованием batch-операций
     */
    private List<CartItemDetailsFacadeResponseDto> enrichCartItems(List<CartItemGetResponseDto> items,
                                                                   Map<Long/*productId*/, Long/*categoryId*/> categoryIdByProductIds) {
        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        // Собираем variantIds для batch-запроса вариантов с продуктами
        Set<Long> variantIds = items.stream()
                .map(CartItemGetResponseDto::variantId)
                .collect(Collectors.toSet());

        // Выполняем batch-запрос вариантов с продуктами (1 запрос вместо N+1)
        Map<Long, ProductVariant> variantMap = fetchVariantsBatch(variantIds);

        Set<Long> priceIds = variantMap.values().stream()
                .map(ProductVariant::getPriceId)
                .collect(Collectors.toSet());
        Map<Long, PriceQuoteDto> priceMap = priceService.getPricesByIds(priceIds);

        Set<Long> mediaIds = variantMap.values().stream()
                .flatMap(variant -> Optional.ofNullable(variant.getProductVariantMedia())
                        .orElse(Collections.emptyList()).stream())
                .map(ProductVariantMedia::getMediaId)
                .collect(Collectors.toSet());

        Map<Long, String> mediaUrlMap = fetchMediaUrlsBatch(mediaIds);

        // Обогащаем каждый элемент с предзагруженными данными
        return items.stream()
                .map(item -> enrichSingleCartItemWithPreloadedData(
                        item,
                        variantMap,
                        priceMap,
                        mediaUrlMap,
                        categoryIdByProductIds.get(item.productId())
                ))
                .toList();
    }

    /**
     * Обогащает отдельный товар корзины с использованием предзагруженных данных
     */
    private CartItemDetailsFacadeResponseDto enrichSingleCartItemWithPreloadedData(
            CartItemGetResponseDto item,
            Map<Long, ProductVariant> variantMap,
            Map<Long, PriceQuoteDto> priceMap,
            Map<Long, String> mediaUrlMap,
            Long categoryId) {

        ProductVariant variant = variantMap.get(item.variantId());

        if (variant == null) {
            log.warn("Missing variant data for cart item {}", item.itemId());
            throw new IllegalStateException("Variant data not found for cart item");
        }

        BigDecimal unitPrice = Optional.ofNullable(priceMap.get(variant.getPriceId()))
                .map(PriceQuoteDto::price)
                .orElse(BigDecimal.ZERO);

        BigDecimal itemTotal = calculateItemTotal(unitPrice, item.quantity());
        String imageUrl = findPrimaryMediaId(variant)
                .map(mediaUrlMap::get)
                .orElse("");

        return createEnrichedCartItem(item, variant, unitPrice, itemTotal, imageUrl, categoryId);
    }

    /**
     * Находит ID основного изображения варианта продукта
     */
    private Optional<Long> findPrimaryMediaId(ProductVariant variant) {
        return findPrimaryMedia(variant)
                .map(ProductVariantMedia::getMediaId);
    }

    /**
     * Находит основное изображение варианта продукта (с минимальным sortOrder)
     */
    private Optional<ProductVariantMedia> findPrimaryMedia(ProductVariant variant) {
        if (variant.getProductVariantMedia() == null || variant.getProductVariantMedia().isEmpty()) {
            log.debug("No media found for variant {}", variant.getId());
            return Optional.empty();
        }

        return variant.getProductVariantMedia().stream()
                .min(Comparator.comparing(ProductVariantMedia::getSortOrder));
    }


    /**
     * Выполняет batch-запрос для получения вариантов продуктов с продуктами
     */
    private Map<Long, ProductVariant> fetchVariantsBatch(Set<Long> variantIds) {
        return productVariantService.getVariantsByIds(variantIds);
    }

    /**
     * Выполняет batch-запрос для получения URL медиафайлов
     */
    private Map<Long, String> fetchMediaUrlsBatch(Set<Long> mediaIds) {
        if (mediaIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<MediaGetInfoResponseDto> mediaList = mediaGetService.getMediasByIds(mediaIds);
        return mediaList.stream()
                .collect(Collectors.toMap(
                        MediaGetInfoResponseDto::id,
                        MediaGetInfoResponseDto::s3Url
                ));
    }


    /**
     * Рассчитывает общую стоимость товара (цена за единицу * количество)
     */
    private BigDecimal calculateItemTotal(BigDecimal unitPrice, Integer quantity) {
        if (unitPrice == null || quantity == null || quantity <= 0) {
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Рассчитывает общую сумму корзины
     */
    private BigDecimal calculateCartTotalAmount(List<CartItemDetailsFacadeResponseDto> items) {
        return items.stream()
                .map(CartItemDetailsFacadeResponseDto::totalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Создает обогащенный объект товара корзины
     */
    private CartItemDetailsFacadeResponseDto createEnrichedCartItem(
            CartItemGetResponseDto item,
            ProductVariant variant,
            BigDecimal unitPrice,
            BigDecimal totalPrice,
            String imageUrl,
            Long categoryId) {

        return new CartItemDetailsFacadeResponseDto(
                item.itemId(),
                item.productId(),
                categoryId,
                item.variantId(),
                item.quantity(),
                unitPrice,
                totalPrice,
                item.addedAt(),
                variant.getProduct().getName(),
                variant.getProduct().getArticleNumber(),
                variant.getColorName(),
                variant.getSize(),
                imageUrl,
                false // TODO: реализовать логику избранного
        );
    }
}