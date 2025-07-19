package ru.melulingerie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.melulingerie.domain.ProductMediaEntity;

import java.util.Optional;

/**
 * Repository interface for managing ProductMedia entities.
 * Provides standard CRUD operations and custom queries for accessing
 * media data associated with products.
 */
@Repository
public interface ProductMediaRepository extends JpaRepository<ProductMediaEntity, Long> {

    /**
     * Finds a product media entry by its SHA-256 file hash.
     * This is used for deduplication to check if a file has already been
     * uploaded.
     * @param fileHash The SHA-256 hash of the file.
     * @return An Optional containing the media entry if found, otherwise empty.
     */
    Optional<ProductMediaEntity> findByFileHash(String fileHash);
}