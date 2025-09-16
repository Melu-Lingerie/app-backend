package ru.melulingerie.facade;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "ru.melulingerie.facade.cart",
        "ru.melulingerie.cart",
        "ru.melulingerie.products",
        "ru.melulingerie.price"
})
@EnableJpaRepositories(basePackages = {
        "ru.melulingerie.cart.repository",
        "ru.melulingerie.products.repository",
        "ru.melulingerie.price.repository"
})
@EntityScan(basePackages = {
        "ru.melulingerie.cart.domain",
        "ru.melulingerie.products.domain",
        "ru.melulingerie.price.domain"
})
public class FacadeTestApplication {
}