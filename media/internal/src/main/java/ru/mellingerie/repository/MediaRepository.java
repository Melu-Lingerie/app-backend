package ru.mellingerie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mellingerie.domain.Media;

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
     * @param fileHash Хэш файла.
     * @return Optional<Media>
     */
    @Query("SELECT m FROM Media m " +
            " LEFT JOIN FETCH m.image" +
            " LEFT JOIN FETCH m.video " +
            "WHERE m.fileHash = :fileHash")
    Optional<Media> findByFileHashAndEntityIdAndEntityType(
            @Param("fileHash") String fileHash);
}
