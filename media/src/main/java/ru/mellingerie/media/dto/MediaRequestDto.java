package ru.mellingerie.media.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record MediaRequestDto(CustomMultipartFile file, UUID requestId, int sortOrder, boolean isPrimary,
                              String uploadedBy) {
}
