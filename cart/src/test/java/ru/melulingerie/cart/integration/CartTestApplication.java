package ru.melulingerie.cart.integration;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "ru.melulingerie.cart")
@EnableJpaRepositories(basePackages = "ru.melulingerie.cart.repository")
@EntityScan(basePackages = "ru.melulingerie.cart.domain")
public class CartTestApplication {
}