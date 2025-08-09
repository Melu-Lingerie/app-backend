package ru.mellingerie.api.media;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.mellingerie.api.common.ApiError;
import ru.mellingerie.api.common.ErrorCode;
import ru.mellingerie.exceptions.files.MediaProcessingException;

import java.util.Map;

@RestControllerAdvice
public class MediaExceptionHandler {

    @ExceptionHandler(MediaProcessingException.class)
    public ResponseEntity<ApiError> handleMedia(MediaProcessingException ex) {
        return new ResponseEntity<>(new ApiError(ErrorCode.MEDIA_PROCESSING_FAILED, ex.getMessage(), Map.of()), HttpStatus.UNPROCESSABLE_ENTITY);
    }
}


