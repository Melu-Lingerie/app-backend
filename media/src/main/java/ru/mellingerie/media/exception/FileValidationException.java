package ru.mellingerie.media.exception;

import java.util.Collections;
import java.util.List;

public class FileValidationException extends RuntimeException {
    private final List<String> errors;

    public FileValidationException(List<String> errors) {
        super(String.join("; ", errors));
        this.errors = List.copyOf(errors);
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}