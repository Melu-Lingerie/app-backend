package ru.melulingerie.products.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Условие, ограничивающее применение промо-правила.
 * Несколько условий внутри одного промо комбинируются логикой AND.
 */
@Entity
@Table(name = "promotion_conditions")
@Getter
@Setter
public class PromotionCondition {

    /**
     * Идентификатор условия.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Ссылка на промо, которому принадлежит данное условие.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    /**
     * Вид условия: PRODUCT_ID, CATEGORY_ID, MIN_QTY, MIN_SUM, USER_SEGMENT и т.п.
     * Определяет тип данных и семантику поля argValue.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ConditionKind kind;

    /**
     * Оператор сравнения: EQ, IN, GTE, LTE, BETWEEN и т.д.
     * Используется движком правил для интерпретации значения.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ConditionOp op;

    /**
     * Значение аргумента условия, сериализованное как строка (например, "123", "12,34,56", "1000.00").
     * Может храниться в JSON, если движок поддерживает сложные структуры.
     */
    @Column(name = "arg_value", length = 1024)
    private String argValue;

    public enum ConditionKind { PRODUCT_ID, CATEGORY_ID, MIN_QTY, MIN_SUM, USER_SEGMENT }
    public enum ConditionOp { EQ, IN, GTE, LTE, BETWEEN }

}
