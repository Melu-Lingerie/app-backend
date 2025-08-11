package ru.melulingerie.facade.media.api;

import ru.melulingerie.facade.media.dto.MediaApiResponseDto;
import ru.melulingerie.facade.media.dto.MediaFacadeRequestDto;

public interface MediaFacadeApi {
    MediaApiResponseDto uploadMedia(MediaFacadeRequestDto request);
}
