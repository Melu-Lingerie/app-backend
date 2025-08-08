package ru.mellingerie.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mellingerie.cart.entity.CartItem;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("SELECT ci " +
            "FROM CartItem ci " +
            "WHERE ci.cartId = :cartId " +
            "AND ci.productId = :productId " +
            "AND ci.variantId = :variantId")
    Optional<CartItem> findExistingItem(
            @Param("cartId") Long cartId,
            @Param("productId") Long productId,
            @Param("variantId") Long variantId);

    List<CartItem> findAllByCartId(Long cartId);

    long countByCartId(Long cartId);

    @Modifying
    @Query("UPDATE CartItem ci " +
            "SET ci.quantity = :quantity," +
            " ci.productPriceId = :priceId, ci.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE ci.id = :itemId")
    void updateCartItemQuantity(
            @Param("itemId") Long itemId,
            @Param("quantity") int quantity,
            @Param("priceId") Long priceId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cartId = :cartId")
    void deleteAllByCartId(@Param("cartId") Long cartId);
}