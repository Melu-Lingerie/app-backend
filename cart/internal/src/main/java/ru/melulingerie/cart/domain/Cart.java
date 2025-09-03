package ru.melulingerie.cart.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "carts", indexes = {
        @Index(name = "idx_carts_user_active", columnList = "user_id, is_deleted"),
        @Index(name = "idx_carts_created", columnList = "created_at"),
        @Index(name = "idx_carts_updated", columnList = "updated_at")
})
@SQLDelete(sql = "UPDATE carts SET is_deleted = true, updated_at = NOW() WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "carts_id_seq")
    @SequenceGenerator(name = "carts_id_seq", sequenceName = "carts_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @PositiveOrZero
    @Column(name = "total_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Setter(AccessLevel.PRIVATE)
    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CartItem> cartItems = new ArrayList<>();

    public Cart(Long userId) {
        this.userId = userId;
        this.totalAmount = BigDecimal.ZERO;
        this.deleted = false;
    }

    public void addItem(CartItem item) {
        cartItems.add(item);
        item.setCart(this);
        recalculateTotalAmount();
    }

    public void removeItem(CartItem item) {
        cartItems.remove(item);
        item.setCart(null);
        recalculateTotalAmount();
    }

    public void clearItems() {
        cartItems.clear();
        this.totalAmount = BigDecimal.ZERO;
    }

    public void recalculateTotalAmount() {
        this.totalAmount = cartItems.stream()
                .filter(item -> !item.isDeleted())
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getItemsCount() {
        return (int) cartItems.stream()
                .filter(item -> !item.isDeleted())
                .count();
    }

    public boolean isEmpty() {
        return getItemsCount() == 0;
    }

    public void markAsDeleted() {
        this.deleted = true;
    }

    public boolean isDeleted() {
        return deleted;
    }
}