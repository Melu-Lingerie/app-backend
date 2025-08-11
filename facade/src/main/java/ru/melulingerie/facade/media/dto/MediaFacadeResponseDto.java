package ru.melulingerie.facade.media.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record MediaFacadeResponseDto(
        UUID fileId,
        String url,
        String message
) {}
