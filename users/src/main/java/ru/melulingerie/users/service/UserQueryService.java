package ru.melulingerie.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.users.entity.User;
import ru.melulingerie.users.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        log.debug("Получение пользователя с ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID {} не найден", userId);
                    return new IllegalArgumentException("Пользователь не найден");
                });
    }
}

