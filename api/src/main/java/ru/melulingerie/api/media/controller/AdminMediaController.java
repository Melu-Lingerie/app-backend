package ru.melulingerie.api.media.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.melulingerie.api.media.resource.AdminMediaResource;
import ru.melulingerie.facade.media.dto.UploadMultipartFileDto;
import ru.melulingerie.facade.media.dto.UploadMediaRequestDto;
import ru.melulingerie.facade.media.dto.UploadMediaResponseDto;
import ru.melulingerie.facade.media.service.MediaUploadFacadeService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminMediaController implements AdminMediaResource {

    private final MediaUploadFacadeService mediaUploadFacadeService;

    @Override
    public ResponseEntity<UploadMediaResponseDto> uploadMedia(
            UploadMultipartFileDto file,
            UUID requestId
    ) {
            UploadMediaRequestDto request = UploadMediaRequestDto.builder()
                    .file(file)
                    .requestId(requestId)
                    .build();

            UploadMediaResponseDto response = mediaUploadFacadeService.uploadMedia(request);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}