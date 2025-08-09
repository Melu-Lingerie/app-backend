package ru.mellingerie.facade.media.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record MediaUploadFacadeResponse(
        UUID fileId,
        String url,
        String message
) {}
