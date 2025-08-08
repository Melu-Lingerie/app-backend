package ru.mellingerie.wishlist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mellingerie.wishlist.entity.Wishlist;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    @Query("select w from Wishlist w where w.userId = :userId")
    Optional<Wishlist> findByUserId(@Param("userId") Long userId);
}


