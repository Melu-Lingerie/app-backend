package ru.melulingerie.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * Represents a user's address, including type, contact details, and metadata.
 * Each record is uniquely associated with a user and an address type.
 * Example usage:
 * <pre>
 *   UserAddress address = new UserAddress(...);
 * </pre>
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "user_addresses")
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_address_user"))
    private User user;

    //TODO обсудить UserAddressType
//    @Enumerated(EnumType.STRING)
//    @Column(name = "addr_type", length = 16, nullable = false)
//    private UserAddressType addrType;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "street", length = 255)
    private String street;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "address_meta", columnDefinition = "jsonb", nullable = false)
    private String addressMeta;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Constructs a new UserAddress.
     *
     * @param id          Unique identifier
     * @param user        User reference
     *                    //     * @param addrType Address type
     * @param firstName   First name
     * @param lastName    Last name
     * @param street      Street address
     * @param city        City name
     * @param state       State or province
     * @param postalCode  Postal or ZIP code
     * @param country     Country name
     * @param isDefault   Default address flag
     * @param addressMeta Address metadata (JSON)
     * @param createdAt   Creation timestamp
     */
    public UserAddress(Long id, User user, String firstName, String lastName, String street, String city, String state, String postalCode, String country, boolean isDefault, String addressMeta, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
        this.street = street;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
        this.isDefault = isDefault;
        this.addressMeta = addressMeta;
        this.createdAt = createdAt;
    }
} 