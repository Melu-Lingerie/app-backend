package ru.melulingerie.exception;

import java.util.List;

public class AggregatedValidationException extends RuntimeException {
    private final List<String> messages;

    public AggregatedValidationException(List<String> messages) {
        super(String.join("; ", messages));
        this.messages = List.copyOf(messages);
    }

    public List<String> getMessages() {
        return messages;
    }
}