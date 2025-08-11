package ru.melulingerie.facade.media.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import ru.melulingerie.facade.media.dto.UploadMediaRequestDto;
import ru.melulingerie.facade.media.dto.UploadMediaResponseDto;
import ru.melulingerie.facade.media.mapper.MediaMapper;
import ru.melulingerie.facade.media.service.MediaUploadFacadeService;
import ru.melulingerie.media.dto.MediaRequestDto;
import ru.melulingerie.media.dto.MediaResponseDto;
import ru.melulingerie.media.service.MediaUploadService;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaUploadFacadeServiceImpl implements MediaUploadFacadeService {

    private final MediaMapper mediaMapper;
    private final MediaUploadService mediaUploadService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public UploadMediaResponseDto uploadMedia(UploadMediaRequestDto request) {
        log.info("Facade layer: Processing upload request with ID: {}", request.requestId());

        MediaRequestDto domainMediaRequest = mediaMapper.toMediaRequestDto(request);

        MediaResponseDto domainMediaResponse = transactionTemplate.execute(status -> mediaUploadService.uploadMedia(domainMediaRequest));

        log.info("Facade layer: Successfully processed request with ID: {}", request.requestId());

        return mediaMapper.toMediaApiResponseDto(domainMediaResponse);
    }
}