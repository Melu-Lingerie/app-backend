package ru.melulingerie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.melulingerie.domain.VideoEntity;

/**
 * Repository interface for managing Video entities.
 * Provides standard CRUD operations for accessing video-specific data.
 */
@Repository
public interface VideoRepository extends JpaRepository<VideoEntity, Long> {
}