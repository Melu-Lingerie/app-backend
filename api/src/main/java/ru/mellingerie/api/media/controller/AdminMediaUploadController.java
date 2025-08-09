package ru.mellingerie.api.media.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.mellingerie.api.media.resource.AdminMediaResource;
import ru.mellingerie.facade.media.dto.CustomMultipartFileFacadeDto;
import ru.mellingerie.facade.media.dto.EntityTypeFacadeDto;
import ru.mellingerie.facade.media.dto.MediaUploadFacadeResponse;
import ru.mellingerie.facade.media.dto.UploadFacadeRequest;
import ru.mellingerie.facade.media.service.MediaUploadFacadeService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminMediaUploadController implements AdminMediaResource {

    private final MediaUploadFacadeService mediaUploadFacadeService;

    @Override
    public ResponseEntity<MediaUploadFacadeResponse> uploadMedia(
            CustomMultipartFileFacadeDto file,
            Long entityId,
            EntityTypeFacadeDto entityType,
            UUID requestId,
            int sortOrder,
            boolean isPrimary
    ) {
        log.info("Получен запрос на загрузку медиа с requestId {}", requestId);

        String adminUser = "admin_user_placeholder";

        UploadFacadeRequest request = UploadFacadeRequest.builder()
                .file(file)
                .requestId(requestId)
                .entityId(entityId)
                .entityType(entityType)
                .sortOrder(sortOrder)
                .isPrimary(isPrimary)
                .uploadedBy(adminUser)
                .build();

        MediaUploadFacadeResponse response = mediaUploadFacadeService.uploadAndSaveMedia(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}