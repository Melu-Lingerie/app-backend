package ru.melulingerie.products.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Правило скидки/акции: описывает тип скидки (процент/фикс и т.д.), период действия,
 * приоритет применения и возможность складывать с другими правилами.
 * Сами условия применения вынесены в PromotionCondition.
 */
@Entity
@Table(name = "promotions")
@Getter
@Setter
public class Promotion {

    /**
     * Идентификатор правила.
     * Используется для связи с условиями и купонами.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Человекочитаемое имя акции для админки и аудита.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Вид скидки: PERCENT (процент), AMOUNT (фикс. сумма), BOGO (купи X — получи Y), TIERED (пороговая).
     * Драйвит алгоритм расчёта.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromotionType type;

    /**
     * Числовое значение скидки. Для PERCENT — "10" означает -10%; для AMOUNT — сумма в валюте.
     * Для сложных типов (BOGO/TIERED) может быть неиспользуемым.
     */
    @Column(name = "value", precision = 19, scale = 4)
    private BigDecimal value;

    /**
     * Приоритет применения: чем меньше число — тем раньше применяется правило.
     * Нужен для детерминированного расчёта при конфликтующих промо.
     */
    private Integer priority;

    /**
     * Признак, можно ли применять вместе с другими промо (stackable=true),
     * либо правило эксклюзивно и блокирует другие скидки (stackable=false).
     */
    private boolean stackable;

    /**
     * Начало действия промо (включительно).
     */
    private Instant validFrom;

    /**
     * Окончание действия промо (исключительно). null — бессрочно.
     */
    private Instant validTo;

    /**
     * Область применения: PRODUCT/CATEGORY/CART.
     * Определяет, на что рассчитаны условия и как брать базовую цену.
     */
    @Column(length = 16)
    private String scope;

    /**
     * Набор условий применения промо (логика AND внутри одного правила).
     * Примеры: минимум суммы, категория, конкретный продукт, сегмент пользователя.
     */
    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PromotionCondition> conditions = new ArrayList<>();

    // геттеры/сеттеры опущены для краткости

    public enum PromotionType { PERCENT, AMOUNT, BOGO, TIERED }
}
