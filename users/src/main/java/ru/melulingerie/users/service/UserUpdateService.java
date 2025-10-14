package ru.melulingerie.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.users.dto.UserUpdateRequestDto;
import ru.melulingerie.users.entity.User;
import ru.melulingerie.users.entity.UserCredentials;
import ru.melulingerie.users.entity.IdentityType;
import ru.melulingerie.users.repository.UserRepository;
import ru.melulingerie.auth.repository.UserCredentialsRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserUpdateService {

    private final UserRepository userRepository;
    private final UserCredentialsRepository credentialsRepository;

    /**
     * Обновляет данные пользователя
     * 
     * @param userId ID пользователя
     * @param request данные для обновления
     * @return обновленный пользователь
     */
    @Transactional
    public User updateUser(Long userId, UserUpdateRequestDto request) {
        log.info("Обновление данных пользователя с ID: {}", userId);

        // 1. Найти пользователя
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        // 2. Проверить, изменился ли email
        boolean emailChanged = !request.email().equals(user.getEmail());
        if (emailChanged) {
            log.info("Email изменен с {} на {}", user.getEmail(), request.email());
            
            // Проверить, что новый email не занят
            Optional<UserCredentials> existingCreds = credentialsRepository
                    .findByIdentifierAndIdentityType(request.email(), IdentityType.EMAIL);
            
            if (existingCreds.isPresent() && !existingCreds.get().getUser().getId().equals(userId)) {
                throw new IllegalArgumentException("Email уже используется другим пользователем");
            }
            
            // TODO: При изменении email нужна повторная верификация
            // Для упрощения пока просто обновляем
            updateEmailCredentials(user, request.email());
        }

        // 3. Проверить, изменился ли телефон
        boolean phoneChanged = !request.phoneNumber().equals(user.getPhoneNumber());
        if (phoneChanged) {
            log.info("Телефон изменен с {} на {}", user.getPhoneNumber(), request.phoneNumber());
            // TODO: При изменении телефона нужна верификация через SMS
        }

        // 4. Обновить данные пользователя
        user.setFirstName(request.firstName());
        user.setMiddleName(request.middleName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());
        user.setBirthDate(request.birthDate());

        User updatedUser = userRepository.save(user);
        log.info("Данные пользователя {} успешно обновлены", userId);

        return updatedUser;
    }

    /**
     * Обновляет email в credentials
     */
    private void updateEmailCredentials(User user, String newEmail) {
        Optional<UserCredentials> emailCredsOpt = credentialsRepository
                .findByIdentifierAndIdentityType(user.getEmail(), IdentityType.EMAIL);
        
        if (emailCredsOpt.isPresent()) {
            UserCredentials emailCreds = emailCredsOpt.get();
            emailCreds.setIdentifier(newEmail);
            // TODO: Сбросить верификацию при изменении email
            // emailCreds.setIsVerified(false);
            credentialsRepository.save(emailCreds);
            log.info("Email credentials обновлены для пользователя {}", user.getId());
        }
    }
}

