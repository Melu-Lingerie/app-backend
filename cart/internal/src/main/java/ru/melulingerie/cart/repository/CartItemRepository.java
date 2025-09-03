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
     * Найти активный элемент корзины по продукту и варианту
     */
    @Query("SELECT ci FROM CartItem ci " +
           "WHERE ci.cart.id = :cartId " +
           "AND ci.productId = :productId " +
           "AND ci.variantId = :variantId " +
           "AND ci.deleted = false " +
           "AND ci.cart.deleted = false")
    Optional<CartItem> findActiveByCartAndProduct(@Param("cartId") Long cartId,
                                                  @Param("productId") Long productId,
                                                  @Param("variantId") Long variantId);

    /**
     * Найти все активные элементы корзины отсортированные по дате добавления
     */
    @Query("SELECT ci FROM CartItem ci " +
           "WHERE ci.cart.id = :cartId " +
           "AND ci.deleted = false " +
           "AND ci.cart.deleted = false " +
           "ORDER BY ci.addedAt DESC")
    List<CartItem> findActiveByCartIdOrderByAddedAtDesc(@Param("cartId") Long cartId);

    /**
     * Найти активные элементы корзины с пагинацией
     */
    @Query("SELECT ci FROM CartItem ci " +
           "WHERE ci.cart.id = :cartId " +
           "AND ci.deleted = false " +
           "AND ci.cart.deleted = false")
    Page<CartItem> findActiveByCartId(@Param("cartId") Long cartId, Pageable pageable);

    /**
     * Мягкое удаление всех элементов корзины
     */
    @Modifying
    @Query("UPDATE CartItem ci SET ci.deleted = true " +
           "WHERE ci.cart.id = :cartId AND ci.deleted = false")
    int softDeleteAllByCartId(@Param("cartId") Long cartId);

    /**
     * Подсчет активных элементов в корзине
     */
    @Query("SELECT COUNT(ci) FROM CartItem ci " +
           "WHERE ci.cart.id = :cartId " +
           "AND ci.deleted = false " +
           "AND ci.cart.deleted = false")
    int countActiveByCartId(@Param("cartId") Long cartId);

    /**
     * Найти активный элемент по ID корзины и ID элемента
     */
    @Query("SELECT ci FROM CartItem ci " +
           "WHERE ci.cart.id = :cartId " +
           "AND ci.id = :itemId " +
           "AND ci.deleted = false " +
           "AND ci.cart.deleted = false")
    Optional<CartItem> findActiveByCartIdAndItemId(@Param("cartId") Long cartId, 
                                                   @Param("itemId") Long itemId);

    /**
     * Мягкое удаление элементов корзины по списку ID
     */
    @Modifying
    @Query("UPDATE CartItem ci SET ci.deleted = true " +
           "WHERE ci.cart.id = :cartId " +
           "AND ci.id IN :itemIds " +
           "AND ci.deleted = false")
    int softDeleteByCartIdAndItemIds(@Param("cartId") Long cartId, 
                                     @Param("itemIds") List<Long> itemIds);

    /**
     * Пакетное обновление количества элементов
     */
    @Modifying
    @Query("UPDATE CartItem ci SET " +
           "ci.quantity = :quantity, " +
           "ci.totalPrice = ci.unitPrice * :quantity " +
           "WHERE ci.id IN :itemIds " +
           "AND ci.deleted = false")
    int batchUpdateQuantity(@Param("itemIds") List<Long> itemIds, 
                           @Param("quantity") Integer quantity);

    /**
     * Найти элементы по списку продуктов
     */
    @Query("SELECT ci FROM CartItem ci " +
           "WHERE ci.cart.id = :cartId " +
           "AND ci.productId IN :productIds " +
           "AND ci.deleted = false")
    List<CartItem> findActiveByCartIdAndProductIds(@Param("cartId") Long cartId,
                                                   @Param("productIds") List<Long> productIds);

    /**
     * Получить общую стоимость корзины
     */
    @Query("SELECT COALESCE(SUM(ci.totalPrice), 0) FROM CartItem ci " +
           "WHERE ci.cart.id = :cartId " +
           "AND ci.deleted = false")
    BigDecimal calculateTotalAmountByCartId(@Param("cartId") Long cartId);

    /**
     * Найти элементы с истекшим сроком резервации (если такая логика нужна)
     */
    @Query("SELECT ci FROM CartItem ci " +
           "WHERE ci.deleted = false " +
           "AND ci.addedAt < :cutoffDate")
    List<CartItem> findExpiredItems(@Param("cutoffDate") java.time.LocalDateTime cutoffDate);
}