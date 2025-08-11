package ru.melulingerie.facade.media.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record MediaApiResponseDto(
        UUID fileId,
        String url,
        String message
) {}
