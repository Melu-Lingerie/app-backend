package ru.melulingerie.facade.media.service;

import ru.melulingerie.facade.media.dto.MediaGetInfoFacadeResponseDto;

import java.util.Collection;
import java.util.Map;

public interface MediaGetFacadeService {
    
    Map<Long, MediaGetInfoFacadeResponseDto> getMediaByIds(Collection<Long> mediaIds);
}