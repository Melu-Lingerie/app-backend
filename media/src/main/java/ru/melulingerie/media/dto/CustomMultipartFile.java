package ru.melulingerie.media.dto;

import lombok.Builder;

@Builder
public record CustomMultipartFile(byte[] content, String originalFilename, String contentType, long size, String name) {
}

