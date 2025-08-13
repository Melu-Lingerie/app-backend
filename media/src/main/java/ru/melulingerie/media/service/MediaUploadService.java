package ru.melulingerie.media.service;

import ru.melulingerie.media.dto.MediaRequestDto;
import ru.melulingerie.media.dto.MediaResponseDto;

public interface MediaUploadService {
    
    MediaResponseDto uploadMedia(MediaRequestDto request);
}