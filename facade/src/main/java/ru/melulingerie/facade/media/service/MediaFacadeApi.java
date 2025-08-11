package ru.melulingerie.facade.media.service;

import ru.melulingerie.facade.media.dto.MediaFacadeResponseDto;
import ru.melulingerie.facade.media.dto.MediaFacadeRequestDto;

public interface MediaFacadeApi {
    MediaFacadeResponseDto uploadMedia(MediaFacadeRequestDto request);
}
