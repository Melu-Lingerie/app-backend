package ru.melulingerie.cart.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.melulingerie.cart.domain.CartItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Найти элемент корзины по продукту и варианту
     */
    @Query("SELECT ci FROM CartItem ci " +
           "WHERE ci.cart.id = :cartId " +
           "AND ci.productId = :productId " +
           "AND ci.variantId = :variantId ")
    Optional<CartItem> findByCartAndProduct(@Param("cartId") Long cartId,
                                            @Param("productId") Long productId,
                                            @Param("variantId") Long variantId);

    /**
     * Найти элемент по ID корзины и ID элемента
     */
    @Query("SELECT ci FROM CartItem ci " +
           "WHERE ci.cart.id = :cartId " +
           "AND ci.id = :itemId")
    Optional<CartItem> findByCartIdAndItemId(@Param("cartId") Long cartId, 
                                             @Param("itemId") Long itemId);

    /**
     * Bulk удаление элементов корзины по ID
     */
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.id IN :itemIds")
    int deleteByCartIdAndItemIds(@Param("cartId") Long cartId, @Param("itemIds") List<Long> itemIds);

}