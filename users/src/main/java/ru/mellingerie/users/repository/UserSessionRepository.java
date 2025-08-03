package ru.mellingerie.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mellingerie.users.entity.UserSession;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

} 