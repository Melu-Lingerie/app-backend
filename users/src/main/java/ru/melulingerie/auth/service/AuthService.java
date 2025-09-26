package ru.melulingerie.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.auth.dto.*;
import ru.melulingerie.auth.entity.EmailVerification;
import ru.melulingerie.auth.entity.RefreshToken;
import ru.melulingerie.auth.repository.EmailVerificationRepository;
import ru.melulingerie.auth.repository.RefreshTokenRepository;
import ru.melulingerie.auth.repository.UserCredentialsRepository;
import ru.melulingerie.users.entity.*;
import ru.melulingerie.users.repository.UserSessionRepository;
import ru.melulingerie.users.service.UserCreateService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserCredentialsRepository credentialsRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserSessionRepository userSessionRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailVerificationService emailVerificationService;
    private final UserCreateService userCreateService;

    /**
     * Найти существующую активную сессию пользователя
     */
    private UserSession findExistingUserSession(User user, UUID sessionId) {
        if (sessionId == null) {
            throw new IllegalArgumentException("SessionId обязателен для аутентификации");
        }
        
        log.info("Поиск сессии с ID: {} для пользователя: {}", sessionId, user.getId());
        
        // Ищем существующую сессию по sessionId
        Optional<UserSession> existingSession = userSessionRepository.findBySessionId(sessionId);
        if (existingSession.isPresent()) {
            UserSession session = existingSession.get();
            log.info("Найдена сессия: ID={}, userId={}, active={}", 
                session.getId(), session.getUser().getId(), session.isActive());
            
            // Проверяем, что сессия принадлежит этому пользователю и активна
            if (session.getUser().getId().equals(user.getId()) && session.isActive()) {
                // Обновляем время активности
                session.touch(Duration.ofHours(24));
                log.info("Переиспользуем существующую сессию: {}", session.getId());
                return userSessionRepository.save(session);
            } else if (!session.getUser().getId().equals(user.getId())) {
                throw new IllegalStateException("Сессия принадлежит другому пользователю");
            } else {
                throw new IllegalStateException("Сессия неактивна или истекла");
            }
        }
        
        log.warn("Сессия с ID {} не найдена", sessionId);
        throw new IllegalStateException("Сессия с ID " + sessionId + " не найдена");
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto dto) {
        // 1) Найти учетные данные по email
        UserCredentials creds = credentialsRepository
                .findByIdentifierAndIdentityType(dto.getEmail(), IdentityType.EMAIL)
                .orElseThrow(() -> new IllegalArgumentException("Неверный email или пароль"));

        User user = creds.getUser();

        // 2) Проверка статуса/верификации
        if (user.getStatus() != UserStatus.ACTIVE || Boolean.FALSE.equals(creds.getIsVerified())) {
            throw new IllegalStateException("Профиль не активирован или email не подтвержден");
        }

        // 3) Сверка пароля (BCrypt)
        if (creds.getPasswordHash() == null || !passwordEncoder.matches(dto.getPassword(), creds.getPasswordHash())) {
            // TODO счетчики failedLogin добавим позже пока просто считаем кол-во неверных запросов
            throw new IllegalArgumentException("Неверный email или пароль");
        }

        // 4) Найти существующую активную сессию или обработать случай без sessionId
        UserSession session;
        if (dto.getSessionId() != null) {
            // Используем переданную сессию
            session = findExistingUserSession(user, dto.getSessionId());
        } else {
            // Для случаев повторного логина без sessionId - требуем, чтобы клиент передавал sessionId
            throw new IllegalArgumentException("SessionId обязателен для логина. Получите sessionId через создание гостевого пользователя.");
        }

        // 5) Сгенерировать токены
        String access = jwtService.generateAccessToken(user, creds);
        String refresh = jwtService.generateRefreshToken(user, creds);

        // 6) Сохранить refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refresh)
                .user(user)
                .userSession(session)            // связываем сессией
                .expiryDate(LocalDateTime.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .build();
        refreshTokenRepository.save(refreshToken);

        // 7) Сформировать ответ
        return LoginResponseDto.builder()
                .userId(user.getId())
                .email(creds.getIdentifier())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .status(user.getStatus())
                .accessToken(access)
                .refreshToken(refresh)
                .accessTokenExpiresIn(jwtService.getAccessTtlSeconds())
                .refreshTokenExpiresIn(jwtService.getRefreshTtlSeconds())
                .build();
    }

    @Transactional
    public RefreshResponseDto refreshAccessToken(RefreshRequestDto dto) {
        RefreshToken oldRt = refreshTokenRepository.findByToken(dto.getRefreshToken())
                .orElseThrow(() -> new IllegalArgumentException("Неверный refresh token"));

        if (oldRt.isExpired()) {
            // Удалим просроченный токен, чтобы не копился мусор
            refreshTokenRepository.delete(oldRt);
            throw new IllegalStateException("Refresh token истёк");
        }

        UserSession session = oldRt.getUserSession();
        if (session == null || session.getStatus() != SessionStatus.ACTIVE) {
            // Защита: refresh должен относиться к активной сессии
            throw new IllegalStateException("Сессия неактивна");
        }

        User user = oldRt.getUser();

        // Вытаскиваем email из старого refresh (subject = email)
        String email = jwtService.extractEmail(oldRt.getToken());

        UserCredentials creds = credentialsRepository
                .findByIdentifierAndIdentityType(email, IdentityType.EMAIL)
                .orElseThrow(() -> new IllegalStateException("Учётка не найдена"));

        // Выдать новый access и новый refresh
        String newAccess = jwtService.generateAccessToken(user, creds);
        String newRefresh = jwtService.generateRefreshToken(user, creds);

        // Создать новую запись refresh для той же сессии
        RefreshToken newRt = RefreshToken.builder()
                .token(newRefresh)
                .user(user)
                .userSession(session)
                .expiryDate(LocalDateTime.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .build();
        refreshTokenRepository.save(newRt);

        // Удалить старый refresh (простая ротация)
        refreshTokenRepository.delete(oldRt);

        return RefreshResponseDto.builder()
                .accessToken(newAccess)
                .accessTokenExpiresIn(jwtService.getAccessTtlSeconds())
                .refreshToken(newRefresh)
                .refreshTokenExpiresIn(jwtService.getRefreshTtlSeconds())
                .build();
    }

    @Transactional
    public void registerUser(RegisterRequestDto dto) {
        log.info("Начинаем регистрацию пользователя с email: {}", dto.getEmail());

        // 1. Проверить статус email и обработать разные сценарии
        log.info("Проверяем существование email: {} для пользователя: {}", dto.getEmail(), dto.getUserId());
        Optional<UserCredentials> existingCreds = credentialsRepository
                .findByIdentifierAndIdentityType(dto.getEmail(), IdentityType.EMAIL);
        
        if (existingCreds.isPresent()) {
            log.info("Найдены существующие credentials для email: {}", dto.getEmail());
            UserCredentials creds = existingCreds.get();
            User existingUser = creds.getUser();
            log.info("Email принадлежит пользователю: {}, verified: {}", existingUser.getId(), creds.getIsVerified());
            
            if (Boolean.TRUE.equals(creds.getIsVerified())) {
                // Email уже подтвержден - регистрация невозможна
                log.warn("Попытка регистрации с уже подтвержденным email: {}", dto.getEmail());
                throw new IllegalArgumentException("Пользователь с таким email уже зарегистрирован");
            }
            
            // Email не подтвержден - проверяем, тот ли это пользователь
            if (existingUser.getId().equals(dto.getUserId())) {
                // Тот же пользователь повторно регистрируется - разрешаем
                log.info("Повторная регистрация пользователя {} с email: {}", dto.getUserId(), dto.getEmail());
                // Удаляем старые неподтвержденные credentials для обновления
                credentialsRepository.delete(creds);
                log.info("Удалены старые неподтвержденные credentials для пользователя: {}", dto.getUserId());
            } else {
                // Другой пользователь пытается зарегистрироваться с чужим неподтвержденным email
                log.warn("Пользователь {} пытается зарегистрироваться с email {}, который используется пользователем {}", 
                    dto.getUserId(), dto.getEmail(), existingUser.getId());
                throw new IllegalArgumentException("Этот email уже используется другим пользователем");
            }
        }
        
        // 2. Найти и обновить данные пользователя через UserCreateService
        User user = userCreateService.updateUserForRegistration(
                dto.getUserId(),
                dto.getFirstName(),
                dto.getMiddleName(), 
                dto.getLastName()
        );
        // 3. Создать учетные данные (без верификации)
        createUserCredentials(user, dto);

        // 4. Отправить код верификации (старые коды удаляются автоматически)
        emailVerificationService.sendVerificationCode(dto.getEmail(), user);

        log.info("Регистрация инициирована для пользователя: {}, код отправлен на email: {}",
                user.getId(), dto.getEmail());
    }

    @Transactional
    public LoginResponseDto verifyEmailAndComplete(VerifyEmailRequestDto dto) {
        log.info("Подтверждение email: {}", dto.getEmail());

        // 1. Найти и проверить код верификации
        EmailVerification verification = emailVerificationService.validateCode(dto.getEmail(), dto.getCode());

        // 2. Активировать пользователя через UserCreateService
        User user = userCreateService.activateUser(verification.getUser());
        
        // 3. Пометить email как подтвержденный
        markCredentialsAsVerified(dto.getEmail());
        //TODO необходимо хранить коды некоторое время
        // 4. Удалить использованный код
        emailVerificationRepository.delete(verification);

        // 5. Автоматически залогинить пользователя, используя существующую сессию
        LoginResponseDto response = createLoginResponse(user, dto.getEmail(), dto.getSessionId());

        log.info("Email подтвержден и пользователь активирован: {}", user.getId());
        return response;
    }

    @Transactional
    public void resendVerificationCode(String email) {
        log.info("Повторная отправка кода для email: {}", email);

        // Найти неподтвержденного пользователя по email
        User user = findUnverifiedUserByEmail(email);

        // Отправить новый код
        emailVerificationService.sendVerificationCode(email, user);
    }

    private void markCredentialsAsVerified(String email) {
        UserCredentials emailCreds = credentialsRepository
                .findByIdentifierAndIdentityType(email, IdentityType.EMAIL)
                .orElseThrow(() -> new IllegalStateException("Email credentials не найдены"));

        emailCreds.setIsVerified(true);
        emailCreds.setVerifiedAt(LocalDateTime.now());
        credentialsRepository.save(emailCreds);
    }

    private void createUserCredentials(User user, RegisterRequestDto dto) {
        // Найти существующие email credentials для обновления или создать новые
        UserCredentials emailCreds = credentialsRepository
                .findByUserAndIdentityType(user, IdentityType.EMAIL)
                .orElse(UserCredentials.builder()
                        .user(user)
                        .identityType(IdentityType.EMAIL)
                        .build());

        emailCreds.setIdentifier(dto.getEmail());
        emailCreds.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        emailCreds.setIsVerified(false);
        emailCreds.setFailedLoginCount(0);
        emailCreds.setVerifiedAt(null); // сбрасываем предыдущую верификацию

        credentialsRepository.save(emailCreds);
        //TODO пока только по почте
        // Создаем phone credentials если указан телефон
//        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().trim().isEmpty()) {
//            UserCredentials phoneCreds = UserCredentials.builder()
//                    .user(user)
//                    .identityType(IdentityType.PHONE)
//                    .identifier(dto.getPhoneNumber())
//                    .isVerified(false)
//                    .failedLoginCount(0)
//                    .build();
//            credentialsRepository.save(phoneCreds);
//        }
    }

    private LoginResponseDto createLoginResponse(User user, String email, UUID sessionId) {
        UserCredentials creds = credentialsRepository
                .findByIdentifierAndIdentityType(email, IdentityType.EMAIL)
                .orElseThrow(() -> new IllegalStateException("Email credentials не найдены"));

        // Найти существующую активную сессию
        UserSession session = findExistingUserSession(user, sessionId);

        // Сгенерировать токены
        String access = jwtService.generateAccessToken(user, creds);
        String refresh = jwtService.generateRefreshToken(user, creds);

        // Сохранить refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refresh)
                .user(user)
                .userSession(session)
                .expiryDate(LocalDateTime.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .build();
        refreshTokenRepository.save(refreshToken);

        return LoginResponseDto.builder()
                .userId(user.getId())
                .email(creds.getIdentifier())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .status(user.getStatus())
                .accessToken(access)
                .refreshToken(refresh)
                .accessTokenExpiresIn(jwtService.getAccessTtlSeconds())
                .refreshTokenExpiresIn(jwtService.getRefreshTtlSeconds())
                .build();
    }

    private User findUnverifiedUserByEmail(String email) {
        UserCredentials creds = credentialsRepository
                .findByIdentifierAndIdentityType(email, IdentityType.EMAIL)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с таким email не найден"));

        User user = creds.getUser();
        if (user.getStatus() != UserStatus.PENDING_VERIFICATION || Boolean.TRUE.equals(creds.getIsVerified())) {
            throw new IllegalStateException("Пользователь уже подтвержден или имеет некорректный статус");
        }

        return user;
    }
}
