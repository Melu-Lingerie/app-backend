package ru.mellingerie.media.api;

import ru.mellingerie.media.dto.MediaRequestDto;
import ru.mellingerie.media.dto.MediaResponseDto;

public interface MediaApi {
    MediaResponseDto uploadMedia(MediaRequestDto request);
}
