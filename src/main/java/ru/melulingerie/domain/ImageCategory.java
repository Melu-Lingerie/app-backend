package ru.melulingerie.domain;

/**
 * Defines the category of an image.
 * This helps in classifying images based on their purpose, such as
 * whether it's a primary product shot, a lifestyle image, or a thumbnail.
 */
public enum ImageCategory {
    /**
     * A standard image of the product.
     */
    PRODUCT,
    /**
     * An image showing the product in a real-world context.
     */
    LIFESTYLE,
    /**
     * A close-up image highlighting specific details of the product.
     */
    DETAIL,
    /**
     * A small, compressed version of an image, used for previews.
     */
    THUMBNAIL,
    /**
     * An image intended for use in a gallery display.
     */
    GALLERY
}