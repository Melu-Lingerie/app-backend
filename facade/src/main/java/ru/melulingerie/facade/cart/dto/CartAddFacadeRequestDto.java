package ru.melulingerie.facade.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartAddFacadeRequestDto(
        @NotNull(message = "Product ID is required")
        @Positive(message = "Product ID must be positive")
        Long productId,
        
        @NotNull(message = "Variant ID is required") 
        @Positive(message = "Variant ID must be positive")
        Long variantId,
        
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        Integer quantity
) {}