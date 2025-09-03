package ru.melulingerie.cart.repository;

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
     * Найти активную корзину пользователя
     */
    @Query("SELECT c FROM Cart c WHERE c.userId = :userId AND c.deleted = false")
    Optional<Cart> findActiveByUserId(@Param("userId") Long userId);

    /**
     * Найти корзину с загрузкой всех активных элементов
     */
    @Query("SELECT c FROM Cart c " +
           "LEFT JOIN FETCH c.cartItems ci " +
           "WHERE c.id = :cartId AND c.deleted = false " +
           "AND (ci.deleted = false OR ci.deleted IS NULL)")
    Optional<Cart> findByIdWithActiveItems(@Param("cartId") Long cartId);

    /**
     * Найти корзину с элементами, отсортированными по дате добавления
     */
    @Query("SELECT c FROM Cart c " +
           "LEFT JOIN FETCH c.cartItems ci " +
           "WHERE c.id = :cartId AND c.deleted = false " +
           "AND (ci.deleted = false OR ci.deleted IS NULL) " +
           "ORDER BY ci.addedAt DESC")
    Optional<Cart> findByIdWithItemsSortedByDate(@Param("cartId") Long cartId);

    /**
     * Найти все активные корзины, неактивные более указанного времени
     */
    @Query("SELECT c FROM Cart c " +
           "WHERE c.deleted = false " +
           "AND c.updatedAt < :cutoffDate")
    List<Cart> findInactiveCartsOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Пакетное обновление общей суммы корзин
     */
    @Modifying
    @Query("UPDATE Cart c SET c.totalAmount = " +
           "(SELECT COALESCE(SUM(ci.totalPrice), 0) FROM CartItem ci " +
           "WHERE ci.cart.id = c.id AND ci.deleted = false) " +
           "WHERE c.id IN :cartIds")
    void batchUpdateTotalAmounts(@Param("cartIds") List<Long> cartIds);

    /**
     * Проверить существование активной корзины пользователя
     */
    @Query("SELECT COUNT(c) > 0 FROM Cart c WHERE c.userId = :userId AND c.deleted = false")
    boolean existsActiveByUserId(@Param("userId") Long userId);

    /**
     * Получить количество элементов в корзине
     */
    @Query("SELECT COUNT(ci) FROM Cart c " +
           "LEFT JOIN c.cartItems ci " +
           "WHERE c.id = :cartId AND c.deleted = false " +
           "AND ci.deleted = false")
    int getItemsCount(@Param("cartId") Long cartId);
}