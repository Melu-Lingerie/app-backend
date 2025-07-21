package ru.melulingerie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.melulingerie.domain.media.ImageEntity;

/**
 * Repository interface for managing Image entities.
 * Provides standard CRUD operations for accessing image-specific data.
 */
@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
}