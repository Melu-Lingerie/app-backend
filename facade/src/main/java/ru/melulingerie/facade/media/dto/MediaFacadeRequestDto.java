package ru.melulingerie.facade.media.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record  MediaFacadeRequestDto(
        CustomMultipartFileFacadeDto file,
        UUID requestId,
        int sortOrder,
        boolean isPrimary,
        String uploadedBy
) {}
