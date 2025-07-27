package ru.melulingerie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.melulingerie.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

} 