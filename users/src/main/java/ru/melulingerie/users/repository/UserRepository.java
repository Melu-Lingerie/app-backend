package ru.melulingerie.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.melulingerie.users.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

} 