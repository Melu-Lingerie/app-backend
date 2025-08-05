package ru.melulingerie.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UploadResult {
    String bucket;
    String key;
    String url;
}