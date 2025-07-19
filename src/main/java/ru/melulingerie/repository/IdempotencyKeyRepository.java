package ru.melulingerie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.melulingerie.domain.IdempotencyKeyEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing IdempotencyKey entities.
 * Provides standard CRUD operations and custom queries for handling
 * idempotent requests.
 */
@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKeyEntity, UUID> {

    /**
     * Finds an idempotency key by its unique key string.
     * This is used to check if a request has already been processed.
     * @param idempotencyKey The unique string identifying the request.
     * @return An Optional containing the key entry if found, otherwise empty.
     */
    Optional<IdempotencyKeyEntity> findByIdempotencyKey(String idempotencyKey);
}