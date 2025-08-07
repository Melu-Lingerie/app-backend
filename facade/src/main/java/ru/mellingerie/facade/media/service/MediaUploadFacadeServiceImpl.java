package ru.mellingerie.facade.media.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mellingerie.facade.media.dto.MediaUploadFacadeResponse;
import ru.mellingerie.facade.media.dto.UploadFacadeRequest;
import ru.mellingerie.facade.media.mapper.MediaFacadeMapper;
import ru.melulingerie.files.dto.MediaUploadResponse;
import ru.melulingerie.files.dto.UploadRequest;
import ru.melulingerie.files.service.MediaService;

@Service
@Slf4j
@RequiredArgsConstructor
public class MediaUploadFacadeServiceImpl implements MediaUploadFacadeService {

    private final MediaService coreMediaService;
    private final MediaFacadeMapper mediaFacadeMapper;

    @Override
    public MediaUploadFacadeResponse uploadAndSaveMedia(UploadFacadeRequest request) {
        log.info("Facade layer: Processing upload request with ID: {}", request.requestId());

        UploadRequest coreRequest = mediaFacadeMapper.toCoreUploadRequest(request);

        MediaUploadResponse coreResponse = coreMediaService.uploadAndSaveMedia(coreRequest);

        MediaUploadFacadeResponse facadeResponse = mediaFacadeMapper.toFacadeMediaUploadResponse(coreResponse);

        log.info("Facade layer: Successfully processed request with ID: {}", request.requestId());
        return facadeResponse;
    }
}