package ru.melulingerie.domain;

/**
 * Represents the processing status of an idempotent request.
 * This is used to track the state of a request to prevent
 * duplicate operations in a distributed system.
 */
public enum ProcessingStatus {
    /**
     * The request is currently being processed.
     */
    PROCESSING,
    /**
     * The request has been completed successfully.
     */
    COMPLETED,
    /**
     * The request processing has failed.
     */
    FAILED
}