package ru.melulingerie.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationErrors {
    private final List<String> errors = new ArrayList<>();

    public static ValidationErrors create() {
        return new ValidationErrors();
    }

    public void add(String message) {
        if (message != null && !message.isBlank()) {
            errors.add(message);
        }
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<String> getAll() {
        return Collections.unmodifiableList(errors);
    }

    public String join(String delimiter) {
        return String.join(delimiter, errors);
    }
}