package ru.melulingerie.facade.media.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mellingerie.media.api.MediaApi;
import ru.melulingerie.facade.media.api.MediaFacadeApi;
import ru.melulingerie.facade.media.dto.MediaApiResponseDto;
import ru.mellingerie.media.dto.MediaResponseDto;
import ru.melulingerie.facade.media.dto.MediaFacadeRequestDto;
import ru.mellingerie.media.dto.MediaRequestDto;
import ru.melulingerie.facade.media.mapper.MediaFacadeMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaFacadeApiImpl implements MediaFacadeApi {

    private final MediaApi mediaApi;
    private final MediaFacadeMapper mediaFacadeMapper;

    @Override
    public MediaApiResponseDto uploadMedia(MediaFacadeRequestDto request) {
        log.info("Facade layer: Processing upload request with ID: {}", request.requestId());

        MediaRequestDto coreRequest = mediaFacadeMapper.toCoreUploadRequest(request);

        MediaResponseDto coreResponse = mediaApi.uploadMedia(coreRequest);

        MediaApiResponseDto facadeResponse = mediaFacadeMapper.toFacadeMediaUploadResponse(coreResponse);

        log.info("Facade layer: Successfully processed request with ID: {}", request.requestId());

        return facadeResponse;
    }
}