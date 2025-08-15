package ru.mellingerie.products.dto;

/**
 * Хлебная крошка для навигации
 */
public record BreadcrumbDTO(
    String name,
    String slug,
    String url
) {} 