package ru.mellingerie.facade.media.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UploadFacadeRequest(
        CustomMultipartFileFacadeDto file,
        UUID requestId,
        Long entityId,
        EntityTypeFacadeDto entityType,
        int sortOrder,
        boolean isPrimary,
        String uploadedBy
) {}
