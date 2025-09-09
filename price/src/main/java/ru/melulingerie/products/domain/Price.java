package ru.melulingerie.products.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;

/**
 * Историческая "базовая" цена товара.
 * Не хранит скидки, только листинговую/закупочную/распродажную базу по периодам действия.
 * На момент времени выбирается единственная актуальная запись (valid_from..valid_to).
 */
@Entity
@Table(
        name = "prices",
        uniqueConstraints = {
                // Защита от пересечения периодов для одного товара.
                @UniqueConstraint(columnNames = {"product_variant_id", "valid_from", "valid_to"})
        },
        indexes = {
                @Index(name = "idx_price_product_period", columnList = "product_variant_id,valid_from,valid_to"),
                @Index(name = "idx_price_current", columnList = "is_current")
        }
)
@Getter
@Setter
public class Price {

    /**
     * Идентификатор цены.
     * Технический PK строки истории.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Идентификатор на вариант товара, для которого установлена данная базовая цена.
     */
    @Column(name = "product_variant_id", nullable = false)
    private Long productVariantId;

    /**
     * Денежное значение (сумма + валюта).
     * Инкапсулирует корректную работу с мультивалютностью и округлением.
     */
    @Embedded
    private Money money;

    /**
     * Тип базовой цены. Примеры: LIST (розничная), SALE (акционная базовая), COST (себестоимость).
     * Помогает различать семантику ряда цен, если их несколько параллельно.
     */
    @Column(name = "price_type", length = 16, nullable = false)
    private String priceType;

    /**
     * Дата/время начала действия базовой цены (включительно).
     * Используется для выборки актуальной цены по "сейчас" или на дату заказа.
     */
    @Column(name = "valid_from", nullable = false)
    private Instant validFrom;

    /**
     * Дата/время окончания действия базовой цены (исключительно). null — бессрочно.
     * При null цена действует от valid_from и далее, пока не появится новая запись.
     */
    @Column(name = "valid_to")
    private Instant validTo;

    /**
     * Быстрый флаг текущей актуальности (для ускорения выборок).
     * Должен поддерживаться сервисной логикой при изменении цен.
     */
    @Column(name = "is_current", nullable = false)
    private boolean current;

    protected Price() {}
    // геттеры/сеттеры опущены для краткости

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Price that)) return false;
        return Objects.equals(id, that.id);
    }
    @Override public int hashCode() { return Objects.hashCode(id); }
}
