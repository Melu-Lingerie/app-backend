package ru.melulingerie.media.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.media.dto.MediaGetInfoResponseDto;
import ru.melulingerie.media.dto.MediaResponseDto;
import ru.melulingerie.media.service.MediaGetService;
import ru.melulingerie.repository.MediaRepository;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaGetServiceImpl implements MediaGetService {

    private final MediaRepository mediaRepository;

    @Override
    public List<MediaGetInfoResponseDto> getMediasByIds(Collection<Long> mediaIds) {
        log.info("Getting medias by IDs: {}", mediaIds);
        var mediaList = mediaRepository.findByIdInAndIsActiveTrueAndIsDeletedFalse(mediaIds);
        log.info("Found {} medias for {} IDs", mediaList.size(), mediaIds.size());
        return mediaList.stream()
                .map(MediaGetInfoResponseDto::fromEntity)
                .toList();
    }
}