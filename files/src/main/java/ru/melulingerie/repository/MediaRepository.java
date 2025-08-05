package ru.melulingerie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.melulingerie.domain.EntityType;
import ru.melulingerie.domain.Media;

import java.util.Optional;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    /**
     * Ищет любую запись с таким хэшем (для дедупликации файла в S3).
     */
    Optional<Media> findFirstByFileHash(String fileHash);

    /**
     * Ищет конкретную связь файла с сущностью (для полной дедупликации записи в БД).
     * Также жадно подгружает связанные сущности Image и Video во избежание N+1.
     *
     * @param fileHash   Хэш файла.
     * @param entityId   ID сущности.
     * @param entityType Тип сущности.
     * @return Optional<Media>
     */
    @Query("SELECT m FROM Media m " +
            " LEFT JOIN FETCH m.image" +
            " LEFT JOIN FETCH m.video " +
            "WHERE m.fileHash = :fileHash AND m.entityId = :entityId AND m.entityType = :entityType")
    Optional<Media> findByFileHashAndEntityIdAndEntityType(
            @Param("fileHash") String fileHash,
            @Param("entityId") Long entityId,
            @Param("entityType") EntityType entityType
    );
}
