package ru.melulingerie.facade.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartCreateService {
    
    public Long createCart(Long userId) {
        // Заглушка - в реальной реализации будет создаваться корзина в модуле cart
        log.info("Создание корзины для пользователя: {}", userId);
        return 1000L + userId;
    }
}
