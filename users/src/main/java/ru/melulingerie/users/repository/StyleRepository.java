package ru.melulingerie.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.melulingerie.users.entity.Style;

@Repository
public interface StyleRepository extends JpaRepository<Style, Long> {

} 