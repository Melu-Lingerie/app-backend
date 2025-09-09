package ru.melulingerie.products.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Денежное значение как value object.
 * Хранит сумму и код валюты ISO-4217; используется в сущностях как @Embedded,
 * чтобы избегать дублирования колонок и обеспечить целостность представления денег.
 */
@Getter
@Embeddable
public class Money {
    /**
     * Сумма в денежной единице.
     * Рекомендуется precision=19, scale=4 для коммерческих операций и избежания переполнений.
     */
    @Column(name = "amount", precision = 19, scale = 4, nullable = false)
    private BigDecimal amount;

    /**
     * Трёхбуквенный код валюты по ISO-4217 (например, "RUB", "USD", "EUR").
     * Нужен для мультивалютных каталогов и для корректной конвертации/отображения.
     */
    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    protected Money() {}
    public Money(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }
}
