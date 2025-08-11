package ru.melulingerie.facade.media.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record MediaApiRequestDto(
        CustomMultipartFileFacadeDto file,
        UUID requestId,
        String uploadedBy
) {}
