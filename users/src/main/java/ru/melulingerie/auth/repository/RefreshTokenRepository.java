package ru.melulingerie.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.melulingerie.auth.entity.RefreshToken;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    
    /**
     * Найти все активные (не отозванные и не истекшие) refresh токены пользователя
     */
    List<RefreshToken> findByUserIdAndIsRevokedFalseOrderByCreatedAtDesc(Long userId);

    //TODO данные методы нужны будут для реализации метода logOut
    void deleteByUserId(Long userId);

    void deleteByUserUserSessionsSessionId(UUID sessionId);
}
