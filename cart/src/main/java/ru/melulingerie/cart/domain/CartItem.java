package ru.melulingerie.cart.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cart_items",
        indexes = {
                @Index(name = "idx_cart_items_cart_product_deleted", columnList = "cart_id, product_id, variant_id, is_deleted"),
                @Index(name = "idx_cart_items_product", columnList = "product_id, variant_id"),
                @Index(name = "idx_cart_items_created", columnList = "added_at")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_cart_items_product_variant",
                        columnNames = {"cart_id", "product_id", "variant_id"})
        })
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_items_id_seq")
    @SequenceGenerator(name = "cart_items_id_seq", sequenceName = "cart_items_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cart_items_cart"))
    private Cart cart;

    @NotNull
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @NotNull
    @Column(name = "variant_id", nullable = false)
    private Long variantId;

    @Positive
    @Column(name = "quantity", nullable = false)
    private Integer quantity;


    @CreationTimestamp
    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime addedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Setter(AccessLevel.PRIVATE)
    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    public CartItem(Cart cart, Long productId, Long variantId, Integer quantity) {
        this.cart = cart;
        this.productId = productId;
        this.variantId = variantId;
        this.quantity = quantity;
        this.deleted = false;
    }

    public void updateQuantity(Integer newQuantity) {
        if (newQuantity == null || newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive: " + newQuantity);
        }
        this.quantity = newQuantity;
    }

    public boolean isSameProduct(Long productId, Long variantId) {
        return Objects.equals(this.productId, productId) && Objects.equals(this.variantId, variantId);
    }

    public void markAsDeleted() {
        this.deleted = true;
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItem cartItem)) return false;
        return Objects.equals(id, cartItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", productId=" + productId +
                ", variantId=" + variantId +
                ", quantity=" + quantity +
                '}';
    }
}