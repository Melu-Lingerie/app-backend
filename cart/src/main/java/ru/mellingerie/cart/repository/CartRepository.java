
package ru.mellingerie.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mellingerie.cart.entity.Cart;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserIdAndIsActiveTrue(Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Cart c SET c.updatedAt = CURRENT_TIMESTAMP WHERE c.id = :cartId")
    int updateCartTimestamp(@Param("cartId") Long cartId);

    /**
     * Проверка существования товара в корзине и принадлежности пользователю
     */
    @Query(value = """
            SELECT EXISTS(
                SELECT 1
                FROM carts c
                JOIN cart_items ci ON c.id = ci.cart_id
                WHERE ci.id = :cartItemId AND c.user_id = :userId
            )
            """, nativeQuery = true)
    boolean cartItemExistsAndBelongsToUser(@Param("cartItemId") Long cartItemId, @Param("userId") Long userId);
}