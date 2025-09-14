package ru.melulingerie.price.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Entity
@Table(name = "prices")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    /**
     * Базовая стоимость товара. Без учета скидок.
     */
    @Column(name = "base_amount")
    private BigDecimal baseAmount;
}
