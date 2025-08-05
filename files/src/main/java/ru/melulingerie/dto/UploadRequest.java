package ru.melulingerie.dto;

import lombok.Builder;
import lombok.Value;
import org.springframework.web.multipart.MultipartFile;
import ru.melulingerie.domain.EntityType;

import java.util.UUID;

@Value
@Builder
public class UploadRequest {
    MultipartFile file;
    UUID requestId;
    Long entityId;
    EntityType entityType;
    int sortOrder;
    boolean isPrimary;
    String uploadedBy;
}
