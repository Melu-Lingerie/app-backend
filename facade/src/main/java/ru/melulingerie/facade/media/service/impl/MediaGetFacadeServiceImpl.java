package ru.melulingerie.facade.media.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.facade.media.dto.MediaGetInfoFacadeResponseDto;
import ru.melulingerie.facade.media.mapper.MediaMapper;
import ru.melulingerie.facade.media.service.MediaGetFacadeService;
import ru.melulingerie.media.dto.MediaGetInfoResponseDto;
import ru.melulingerie.media.dto.MediaResponseDto;
import ru.melulingerie.media.service.MediaGetService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaGetFacadeServiceImpl implements MediaGetFacadeService {

    private final MediaMapper mediaMapper;
    private final MediaGetService mediaGetService;

    @Override
    public Map<Long, MediaGetInfoFacadeResponseDto> getMediaByIds(Collection<Long> mediaIds) {
        log.info("Facade layer: Getting medias by IDs: {}", mediaIds);
        
        List<MediaGetInfoResponseDto> mediaList = mediaGetService.getMediasByIds(mediaIds);
        
        Map<Long, MediaGetInfoFacadeResponseDto> mediaInfoById = mediaList.stream()
                .map(mediaMapper::toMediaInfo)
                .collect(Collectors.toMap(MediaGetInfoFacadeResponseDto::id, Function.identity()));
        
        log.info("Facade layer: Found {} medias for {} IDs", mediaInfoById.size(), mediaIds.size());
        return mediaInfoById;
    }
}