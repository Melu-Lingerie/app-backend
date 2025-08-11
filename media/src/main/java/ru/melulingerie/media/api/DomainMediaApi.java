package ru.melulingerie.media.api;

import ru.melulingerie.media.dto.MediaRequestDto;
import ru.melulingerie.media.dto.MediaResponseDto;

public interface DomainMediaApi {
    MediaResponseDto uploadMedia(MediaRequestDto request);
}
