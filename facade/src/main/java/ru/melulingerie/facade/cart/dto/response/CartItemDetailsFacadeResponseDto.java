package ru.melulingerie.facade.cart.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(
        name = "CartItemDetailsFacadeResponseDto",
        description = "Детальная информация по позиции корзины"
)
public record CartItemDetailsFacadeResponseDto(

        @Schema(
                description = "Идентификатор позиции корзины",
                example = "55501",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long itemId,

        @Schema(
                description = "Идентификатор товара",
                example = "12345",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long productId,

        @Schema(
                description = "Идентификатор категории",
                example = "12345",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long categoryId,

        @Schema(
                description = "Идентификатор варианта товара (цвет/размер)",
                example = "98765",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long variantId,

        @Schema(
                description = "Количество единиц товара в позиции",
                example = "2",
                minimum = "0",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Integer quantity,

        @Schema(
                description = "Цена за единицу товара",
                example = "1990.00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        BigDecimal unitPrice,

        @Schema(
                description = "Итоговая цена позиции (quantity × unitPrice)",
                example = "3980.00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        BigDecimal totalPrice,

        @Schema(
                description = "Дата/время добавления позиции (ISO-8601)",
                example = "2025-09-21T18:30:00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        LocalDateTime addedAt,

        @Schema(
                description = "Наименование товара",
                example = "Бюстгальтер push-up",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String productName,

        @Schema(
                description = "Артикул товара (SKU)",
                example = "BR-PSH-001-BLK-75B",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String productSku,

        @Schema(
                description = "Цвет варианта",
                example = "Black",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String variantColor,

        @Schema(
                description = "Размер варианта",
                example = "75B",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String variantSize,

        @Schema(
                description = "URL изображения товара",
                example = "https://cdn.example.com/images/BR-PSH-001-BLK-75B.jpg",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String imageUrl,

        @Schema(
                description = "Признак, что товар в избранном",
                example = "false",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Boolean isFavorite

) {}
