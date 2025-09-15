package ru.melulingerie.price.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.melulingerie.price.domain.Price;

import java.util.List;
import java.util.Set;

public interface PriceRepository extends JpaRepository<Price, Long> {
    @Query("select price from Price price where price.id in :priceIds")
    List<Price> findAllByIds(@Param("priceIds") Set<Long> priceIds);
}
