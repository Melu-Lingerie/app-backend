package ru.melulingerie.wishlist.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.melulingerie.wishlist.domain.WishlistItem;

import java.util.List;
import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    @Query("select wi from WishlistItem wi " +
            "where wi.wishlist.id = :wishlistId " +
            "and wi.productId = :productId " +
            "and wi.variantId = :variantId")
    Optional<WishlistItem> findDuplicate(@Param("wishlistId") Long wishlistId,
                                         @Param("productId") Long productId,
                                         @Param("variantId") Long variantId);

    @Query("select wi from WishlistItem wi where wi.wishlist.id = :wishlistId order by wi.addedAt desc")
    List<WishlistItem> findAllByWishlistId(@Param("wishlistId") Long wishlistId);

    @Query("select wi from WishlistItem wi where wi.wishlist.id = :wishlistId")
    Page<WishlistItem> findByWishlistId(@Param("wishlistId") Long wishlistId, Pageable pageable);

    @Modifying
    @Query("delete from WishlistItem wi where wi.wishlist.id = :wishlistId")
    int deleteAllByWishlistId(@Param("wishlistId") Long wishlistId);

    @Query("select count(wi) from WishlistItem wi where wi.wishlist.id = :wishlistId")
    int countByWishlistId(@Param("wishlistId") Long wishlistId);
}


