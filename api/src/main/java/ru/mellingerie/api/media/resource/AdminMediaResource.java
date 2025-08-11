package ru.mellingerie.api.media.resource;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.melulingerie.facade.media.dto.CustomMultipartFileFacadeDto;
import ru.melulingerie.facade.media.dto.MediaFacadeResponseDto;

import java.util.UUID;

@RequestMapping("/api/v1/admin/media")
public interface AdminMediaResource {

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<MediaFacadeResponseDto> uploadMedia(
            @RequestParam("file") CustomMultipartFileFacadeDto file,
            @RequestHeader("X-Request-Id") UUID requestId,
            @RequestParam(defaultValue = "0", name = "sort_order") int sortOrder,
            @RequestParam(defaultValue = "false", name = "is_primary") boolean isPrimary
    );
}