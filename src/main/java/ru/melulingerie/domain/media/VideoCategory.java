package ru.melulingerie.domain.media;

/**
 * Defines the category of a video.
 * This is used to classify videos based on their content, such as
 * product demonstrations, tutorials, or customer reviews.
 */
public enum VideoCategory {
    /**
     * A video demonstrating the product's features and usage.
     */
    DEMO,
    /**
     * A video showing the product in a real-world scenario.
     */
    LIFESTYLE,
    /**
     * A video providing instructions or guidance on using the product.
     */
    TUTORIAL,
    /**
     * A video review of the product from a user or expert.
     */
    REVIEW,
    /**
     * A video showing the unboxing of the product.
     */
    UNBOXING
}