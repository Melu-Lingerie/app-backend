package ru.melulingerie.files.dto;

import lombok.Builder;

@Builder
public record UploadResult(String bucket, String key, String url) {
}