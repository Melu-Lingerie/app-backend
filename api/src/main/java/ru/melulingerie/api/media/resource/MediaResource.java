package ru.melulingerie.api.media.resource;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.melulingerie.facade.media.dto.UploadMultipartFileDto;
import ru.melulingerie.facade.media.dto.UploadMediaResponseDto;

import java.util.UUID;

@RequestMapping("/api/v1/media")
public interface MediaResource {

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UploadMediaResponseDto> uploadMedia(
            @RequestParam("file") UploadMultipartFileDto file,
            @RequestHeader("X-Request-Id") UUID requestId
    );
}