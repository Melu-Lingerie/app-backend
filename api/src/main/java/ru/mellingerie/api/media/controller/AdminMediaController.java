package ru.mellingerie.api.media.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.mellingerie.api.media.resource.AdminMediaResource;
import ru.melulingerie.facade.media.dto.CustomMultipartFileFacadeDto;
import ru.melulingerie.facade.media.dto.MediaFacadeRequestDto;
import ru.melulingerie.facade.media.dto.MediaApiResponseDto;
import ru.melulingerie.facade.media.api.MediaFacadeApi;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminMediaController implements AdminMediaResource {

    private final MediaFacadeApi mediaFacadeApi;

    @Override
    public ResponseEntity<MediaApiResponseDto> uploadMedia(
            CustomMultipartFileFacadeDto file,
            UUID requestId,
            int sortOrder,
            boolean isPrimary
    ) {
        log.info("Получен запрос на загрузку медиа с requestId {}", requestId);

        MediaFacadeRequestDto request = MediaFacadeRequestDto.builder()
                .file(file)
                .requestId(requestId)
                .sortOrder(sortOrder)
                .isPrimary(isPrimary)
                .uploadedBy("")
                .build();

        MediaApiResponseDto response = mediaFacadeApi.uploadMedia(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}