package ru.mellingerie.cart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface CartItemTotal {
    Long getId();
    Long getProductId();
    String getName();
    String getSlug();
    Long getVariantId();
    String getColorName();
    String getColorHex();
    String getSize();
    Integer getQuantity();
    BigDecimal getPrice();
    BigDecimal getTotal();
    String getImageUrl();
    LocalDateTime getAddedAt();
    String getStockStatus();
}
