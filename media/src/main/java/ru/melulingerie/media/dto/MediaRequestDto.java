package ru.melulingerie.media.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record MediaRequestDto(CustomMultipartFile file, UUID requestId, String uploadedBy) {
}
