package ru.melulingerie.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.melulingerie.domain.subscription.SubscriptionStatus;
import ru.melulingerie.domain.subscription.SubscriptionType;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a user's subscription, including type, status, frequency, and delivery schedule.
 * Each record is uniquely associated with a user and a subscription type.
 * Example usage:
 * <pre>
 *   UserSubscription sub = new UserSubscription(...);
 * </pre>
 */
@Getter
@Entity
@Table(name = "user_subscriptions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "subscription_type"}, name = "uq_user_subscription")
})
public class UserSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_subscription_user"))
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_type", nullable = false)
    private SubscriptionType subscriptionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;

    @Column(name = "frequency", length = 16)
    private String frequency;

    @Column(name = "next_delivery_date")
    private LocalDate nextDeliveryDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Constructs a new immutable UserSubscription.
     * @param id Unique identifier
     * @param user User reference
     * @param subscriptionType Subscription type
     * @param status Subscription status
     * @param frequency Delivery frequency
     * @param nextDeliveryDate Next delivery date
     * @param createdAt Creation timestamp
     * @param updatedAt Update timestamp
     */
    public UserSubscription(Long id, User user, SubscriptionType subscriptionType, SubscriptionStatus status, String frequency, LocalDate nextDeliveryDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.subscriptionType = subscriptionType;
        this.status = status;
        this.frequency = frequency;
        this.nextDeliveryDate = nextDeliveryDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}