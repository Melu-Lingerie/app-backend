package ru.mellingerie.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mellingerie.users.entity.Style;

@Repository
public interface StyleRepository extends JpaRepository<Style, Long> {

} 