package ru.mellingerie.products.dto;

/**
 * Медиа контент товара (изображения и видео)
 */
public record MediaDTO(
    String type, // "image" | "video"
    String url,
    String webpUrl,
    String srcSet,
    String alt,
    Integer sortOrder,
    String thumbnail, // для видео
    String hlsUrl     // для видео
) {} 