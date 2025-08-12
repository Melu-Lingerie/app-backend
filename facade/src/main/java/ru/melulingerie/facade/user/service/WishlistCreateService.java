package ru.melulingerie.facade.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistCreateService {
    
    public Long createWishlist(Long userId) {
        // Заглушка - в реальной реализации будет создаваться список желаний в модуле wishlist
        log.info("Создание списка желаний для пользователя: {}", userId);
        return 2000L + userId;
    }
}
