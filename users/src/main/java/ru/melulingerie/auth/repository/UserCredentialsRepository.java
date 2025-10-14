package ru.melulingerie.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.melulingerie.users.entity.IdentityType;
import ru.melulingerie.users.entity.UserCredentials;

import java.util.Optional;

@Repository
public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Long> {

    @Query("SELECT uc FROM UserCredentials uc JOIN FETCH uc.user WHERE uc.identifier = :identifier AND uc.identityType = :identityType")
    Optional<UserCredentials> findByIdentifierAndIdentityType(@Param("identifier") String identifier, @Param("identityType") IdentityType identityType);

    boolean existsByIdentifierAndIdentityType(String identifier, IdentityType identityType);
    
    Optional<UserCredentials> findByUserAndIdentityType(ru.melulingerie.users.entity.User user, IdentityType identityType);
} 