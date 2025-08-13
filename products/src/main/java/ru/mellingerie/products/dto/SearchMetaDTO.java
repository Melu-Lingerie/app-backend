package ru.mellingerie.products.dto;

/**
 * Метаданные поиска
 */
public record SearchMetaDTO(
    String query,
    Long executionTime // в миллисекундах
) {} 