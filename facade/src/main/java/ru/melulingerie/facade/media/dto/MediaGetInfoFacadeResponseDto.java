package ru.melulingerie.facade.media.dto;

public record MediaGetInfoFacadeResponseDto(
        Long id,
        String fileName,
        String s3Url
) {
}