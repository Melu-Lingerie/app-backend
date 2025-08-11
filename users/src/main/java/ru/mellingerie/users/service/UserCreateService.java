package ru.mellingerie.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mellingerie.users.entity.User;
import ru.mellingerie.users.entity.UserRole;
import ru.mellingerie.users.entity.UserStatus;
import ru.mellingerie.users.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCreateService {
    
    private final UserRepository userRepository;
    
    @Transactional
    public User createGuestUser() {
        User user = User.builder()
                .role(UserRole.GUEST)
                .status(UserStatus.UNREGISTERED)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("Создан пользователь-гость с ID: {}", savedUser.getId());
        return savedUser;
    }
} 