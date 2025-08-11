package ru.mellingerie.media.dto;

import lombok.Builder;

@Builder
public record MediaUploadResponseDto(String bucket, String key, String url) {
}