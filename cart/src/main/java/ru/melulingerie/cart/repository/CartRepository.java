package ru.melulingerie.cart.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.melulingerie.cart.domain.Cart;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Найти корзину с элементами, отсортированными по дате добавления
     */
    @EntityGraph(attributePaths = {"cartItems"})
    @Query("SELECT c FROM Cart c " +
           "LEFT JOIN FETCH c.cartItems ci " +
           "WHERE c.id = :cartId AND c.deleted = false " +
           "AND (ci.deleted = false OR ci.deleted IS NULL) " +
           "ORDER BY ci.addedAt DESC")
    Optional<Cart> findCartByIdWithItemsSortedByDate(@Param("cartId") Long cartId);
}