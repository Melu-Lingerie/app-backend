package ru.melulingerie.facade.media.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UploadMediaResponseDto(
        UUID fileId,
        String url,
        String message
) {}
