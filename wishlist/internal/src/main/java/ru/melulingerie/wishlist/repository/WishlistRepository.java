package ru.melulingerie.wishlist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.melulingerie.wishlist.domain.Wishlist;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    @Query("select w from Wishlist w where w.userId = :userId")
    Optional<Wishlist> findByUserId(@Param("userId") Long userId);

    @Query("SELECT w FROM Wishlist w LEFT JOIN FETCH w.wishlistItems WHERE w.id = :wishlistId")
    Optional<Wishlist> findByIdWithAllItems(@Param("wishlistId") Long wishlistId);

    @Query("select w from Wishlist w left join fetch w.wishlistItems wi " +
           "where w.id = :wishlistId " +
           "order by wi.addedAt desc")
    Optional<Wishlist> findByIdWithItems(@Param("wishlistId") Long wishlistId);

    @Query("select w from Wishlist w join fetch w.wishlistItems wi " +
           "where w.id = :wishlistId and wi.id = :itemId")
    Optional<Wishlist> findByIdWithSpecificItem(@Param("wishlistId") Long wishlistId, @Param("itemId") Long itemId);

}


