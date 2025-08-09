package ru.melulingerie.files.dto;

import lombok.Builder;
import ru.melulingerie.files.domain.EntityType;

import java.util.UUID;

@Builder
public record UploadRequest(CustomMultipartFile file, UUID requestId, Long entityId, EntityType entityType,
                            int sortOrder, boolean isPrimary, String uploadedBy) {
}
