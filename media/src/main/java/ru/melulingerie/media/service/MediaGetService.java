package ru.melulingerie.media.service;

import ru.melulingerie.media.dto.MediaGetInfoResponseDto;
import ru.melulingerie.media.dto.MediaResponseDto;

import java.util.Collection;
import java.util.List;

public interface MediaGetService {
    
    List<MediaGetInfoResponseDto> getMediasByIds(Collection<Long> mediaIds);
}