package ru.melulingerie.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.melulingerie.auth.entity.RefreshToken;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    //TODO нужны буду для реализации метода logOut
    void deleteByUserId(Long userId);

    void deleteByUserSessionSessionId(UUID sessionId);
}
