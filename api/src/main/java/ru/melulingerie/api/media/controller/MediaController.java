package ru.melulingerie.api.media.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.melulingerie.api.media.resource.MediaResource;
import ru.melulingerie.facade.media.dto.UploadMultipartFileDto;
import ru.melulingerie.facade.media.dto.UploadMediaRequestDto;
import ru.melulingerie.facade.media.dto.UploadMediaResponseDto;
import ru.melulingerie.facade.media.service.MediaUploadFacadeService;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MediaController implements MediaResource {

    private final MediaUploadFacadeService mediaUploadFacadeService;

    @Override
    public ResponseEntity<UploadMediaResponseDto> uploadMedia(
            MultipartFile file,
            UUID requestId
    ) {
        try {
            UploadMultipartFileDto fileDto = UploadMultipartFileDto.builder()
                    .inputStream(file.getInputStream())
                    .originalFilename(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .name(file.getName())
                    .build();

            UploadMediaRequestDto request = UploadMediaRequestDto.builder()
                    .file(fileDto)
                    .requestId(requestId)
                    .build();

            UploadMediaResponseDto response = mediaUploadFacadeService.uploadMedia(request);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при обработке файла", e);
        }
    }
}