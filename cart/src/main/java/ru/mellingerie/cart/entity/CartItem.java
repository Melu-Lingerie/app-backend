package ru.mellingerie.cart.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_item_id_seq")
    @SequenceGenerator(name = "cart_item_id_seq", sequenceName = "cart_item_id_seq", allocationSize = 10)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "cart_id", nullable = false)
    private Long cartId;
    
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    @Column(name = "variant_id", nullable = false)
    private Long variantId;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "product_price_id", nullable = false)
    private Long productPriceId;
    
    @CreationTimestamp
    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime addedAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
} 