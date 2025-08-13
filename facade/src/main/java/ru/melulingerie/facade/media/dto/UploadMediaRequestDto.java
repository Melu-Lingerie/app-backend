package ru.melulingerie.facade.media.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UploadMediaRequestDto(
        UploadMultipartFileDto file,
        UUID requestId,
        String uploadedBy
) {}
