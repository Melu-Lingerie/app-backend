package ru.mellingerie.cart.dto;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Ответ при очистке корзины
 */
@Builder
public record ClearCartResponse(
        /**
         * ID пользователя
         */
        Long userId,
        /**
         * ID корзины, которая была очищена
         */
        Long cartId,
        /**
         * Количество товаров, которые были удалены
         */
        Integer itemsRemoved,
        /**
         * Общая стоимость удаленных товаров
         */
        String totalValueRemoved,
        /**
         * Сообщение об успешной очистке
         */
        String message,
        /**
         * Время очистки корзины
         */
        LocalDateTime clearedAt,
        /**
         * Статус операции
         */
        String status
) {}
