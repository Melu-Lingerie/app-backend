package ru.melulingerie.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.melulingerie.domain.EntityType;
import ru.melulingerie.dto.MediaUploadResponse;
import ru.melulingerie.dto.UploadRequest;
import ru.melulingerie.service.MediaService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/media")
@RequiredArgsConstructor
public class AdminMediaController {

    private final MediaService mediaService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaUploadResponse> uploadMedia(
            @RequestParam("file") MultipartFile file,
            @RequestParam("entity_id") Long entityId,
            @RequestParam("entity_type") EntityType entityType,
            @RequestHeader("X-Request-Id") UUID requestId,
            @RequestParam(defaultValue = "0", name = "sort_order") int sortOrder,
            @RequestParam(defaultValue = "false", name = "is_primary") boolean isPrimary
    ) {
        // В реальной системе здесь будет проверка аутентификации/авторизации администратора
        String adminUser = "admin_user_placeholder";

        UploadRequest request = UploadRequest.builder()
                .file(file)
                .requestId(requestId)
                .entityId(entityId)
                .entityType(entityType)
                .sortOrder(sortOrder)
                .isPrimary(isPrimary)
                .uploadedBy(adminUser)
                .build();

        MediaUploadResponse response = mediaService.uploadAndSaveMedia(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
