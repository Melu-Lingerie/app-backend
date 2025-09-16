package ru.melulingerie.cart.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.melulingerie.cart.domain.Cart;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Найти корзину с элементами, отсортированными по дате добавления
     */
    @Query("SELECT c FROM Cart c " +
           "LEFT JOIN FETCH c.cartItems ci " +
           "WHERE c.id = :cartId " +
           "ORDER BY ci.addedAt DESC")
    Optional<Cart> findCartByIdWithItemsSortedByDate(@Param("cartId") Long cartId);

    /**
     * Найти активную корзину пользователя
     */
    @Query("SELECT c FROM Cart c WHERE c.userId = :userId")
    Optional<Cart> findByUserId(@Param("userId") Long userId);
}