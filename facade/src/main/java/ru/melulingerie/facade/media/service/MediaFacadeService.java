package ru.melulingerie.facade.media.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.facade.media.dto.MediaApiRequestDto;
import ru.melulingerie.facade.media.dto.MediaApiResponseDto;
import ru.melulingerie.facade.media.mapper.MediaMapper;
import ru.melulingerie.media.api.DomainMediaApi;
import ru.melulingerie.media.dto.MediaRequestDto;
import ru.melulingerie.media.dto.MediaResponseDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaFacadeService {

    private final DomainMediaApi domainMediaApi;
    private final MediaMapper mediaMapper;

    public MediaApiResponseDto uploadMedia(MediaApiRequestDto request) {
        log.info("Facade layer: Processing upload request with ID: {}", request.requestId());

        MediaRequestDto coreRequest = mediaMapper.toMediaRequestDto(request);

        MediaResponseDto coreResponse = domainMediaApi.uploadMedia(coreRequest);

        MediaApiResponseDto facadeResponse = mediaMapper.toMediaApiResponseDto(coreResponse);

        log.info("Facade layer: Successfully processed request with ID: {}", request.requestId());

        return facadeResponse;
    }
}
