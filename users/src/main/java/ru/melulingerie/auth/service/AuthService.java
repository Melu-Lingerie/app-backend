package ru.melulingerie.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
import java.util.List;
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
    private final TokenHashService tokenHashService;

    /**
     * Найти существующую активную сессию пользователя или создать новую
     * Логика:
     * 1. Если сессия найдена - обновляем активность и продлеваем срок действия
     * 2. Если сессия не найдена - создаем новую
     */
    private UserSession findOrCreateUserSession(User user, UUID sessionId) {
        if (sessionId == null) {
            log.warn("SessionId null для пользователя: {}. Создаем новую сессию", user.getId());
            return createNewSession(user, UUID.randomUUID());
        }
        
        log.info("Поиск сессии с ID: {} для пользователя: {}", sessionId, user.getId());
        
        // Ищем существующую сессию по sessionId
        Optional<UserSession> existingSession = userSessionRepository.findBySessionId(sessionId);
        if (existingSession.isPresent()) {
            UserSession session = existingSession.get();
            
            // Обновляем время активности и продлеваем срок действия
            session.touch(Duration.ofHours(24));
            session.setStatus(SessionStatus.ACTIVE);
            log.info("Обновлена сессия: {}", session.getId());
            return userSessionRepository.save(session);
        }
        
        // Сессия не найдена - создаем новую
        log.info("Сессия с ID {} не найдена, создаем новую для пользователя: {}", sessionId, user.getId());
        return createNewSession(user, sessionId);
    }

    /**
     * Создать новую сессию для пользователя
     */
    private UserSession createNewSession(User user, UUID sessionId) {
        UserSession newSession = UserSession.builder()
                .sessionId(sessionId)
                .user(user)
                .status(SessionStatus.ACTIVE)
                .build();
        
        UserSession savedSession = userSessionRepository.save(newSession);
        log.info("Создана новая сессия: {} для пользователя: {}", savedSession.getSessionId(), user.getId());
        return savedSession;
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
            // Увеличиваем счетчик неверных попыток
            incrementFailedLoginCount(creds);
            log.warn("Неверный пароль для пользователя с email: {}. Количество неверных попыток: {}", 
                dto.getEmail(), creds.getFailedLoginCount());
            throw new IllegalArgumentException("Неверный email или пароль");
        }

        // 4) Найти или создать сессию пользователя
        findOrCreateUserSession(user, dto.getSessionId());

        // 4.1) Сбросить счетчик неверных попыток при успешном логине
        resetFailedLoginCount(creds);
        
        // 5) Сгенерировать токены
        String access = jwtService.generateAccessToken(user, creds);
        String refresh = jwtService.generateRefreshToken(user, creds);

        // 6) Сохранить refresh token (хэшированный)
        String tokenHash = tokenHashService.hashToken(refresh);
        RefreshToken refreshToken = RefreshToken.builder()
                .tokenHash(tokenHash)
                .user(user)
                .expiryDate(LocalDateTime.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .build();

        refreshTokenRepository.save(refreshToken);
        log.info("Сохранен новый refresh token для пользователя: {}", user.getId());

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
        // Извлекаем userId из токена для оптимизации поиска
        Long userId;
        try {
            userId = jwtService.extractUserId(dto.getRefreshToken());
        } catch (Exception e) {
            log.warn("Невозможно извлечь userId из refresh token: {}", e.getMessage());
            throw new IllegalArgumentException("Неверный refresh token");
        }
        
        // Находим все активные токены пользователя
        List<RefreshToken> userTokens = refreshTokenRepository.findByUserIdAndIsRevokedFalseOrderByCreatedAtDesc(userId);
        
        // Проверяем каждый хэш до нахождения совпадения
        RefreshToken oldRt = userTokens.stream()
                .filter(rt -> !rt.isExpired() && tokenHashService.matches(dto.getRefreshToken(), rt.getTokenHash()))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Не найден действительный refresh token для пользователя: {}", userId);
                    return new IllegalArgumentException("Неверный refresh token");
                });
        
        log.info("Найден действительный refresh token для пользователя: {}", userId);

        if (oldRt.isExpired()) {
            // Удалим просроченный токен, чтобы не копился мусор
            refreshTokenRepository.delete(oldRt);
            throw new IllegalStateException("Refresh token истёк");
        }

        User user = oldRt.getUser();

        // Вытаскиваем email из токена (subject = email)
        String email = jwtService.extractEmail(dto.getRefreshToken());

        UserCredentials creds = credentialsRepository
                .findByIdentifierAndIdentityType(email, IdentityType.EMAIL)
                .orElseThrow(() -> new IllegalStateException("Учётка не найдена"));

        // Выдать новый access и новый refresh
        String newAccess = jwtService.generateAccessToken(user, creds);
        String newRefresh = jwtService.generateRefreshToken(user, creds);

        // Создать новую запись refresh (хэшированную)
        String newTokenHash = tokenHashService.hashToken(newRefresh);
        RefreshToken newRt = RefreshToken.builder()
                .tokenHash(newTokenHash)
                .user(user)
                .expiryDate(LocalDateTime.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .build();

        refreshTokenRepository.save(newRt);
        log.info("Создан новый refresh token для пользователя: {}", user.getId());

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
                // Очищаем existingCreds, так как сущность была удалена
                existingCreds = Optional.empty();
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
        createUserCredentials(user, dto, existingCreds);

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

    private void createUserCredentials(User user, RegisterRequestDto dto, Optional<UserCredentials> existingCreds) {
        // Используем существующие credentials или создаем новые
        UserCredentials emailCreds = existingCreds
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

        // Найти или создать сессию пользователя
        findOrCreateUserSession(user, sessionId);

        // Сбросить счетчик неверных попыток при успешной активации
        resetFailedLoginCount(creds);
        
        // Сгенерировать токены
        String access = jwtService.generateAccessToken(user, creds);
        String refresh = jwtService.generateRefreshToken(user, creds);

        // Сохранить refresh token (хэшированный)
        String tokenHash = tokenHashService.hashToken(refresh);
        RefreshToken refreshToken = RefreshToken.builder()
                .tokenHash(tokenHash)
                .user(user)
                .expiryDate(LocalDateTime.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .build();
        refreshTokenRepository.save(refreshToken);
        log.info("Сохранен refresh token при подтверждении email для пользователя: {}", user.getId());

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

    /**
     * Увеличить счетчик неверных попыток входа в отдельной транзакции
     * Используется REQUIRES_NEW чтобы изменения сохранились даже при откате основной транзакции
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void incrementFailedLoginCount(UserCredentials credentials) {
        // Перезагружаем credentials из БД для получения актуального состояния
        UserCredentials freshCredentials = credentialsRepository.findById(credentials.getId())
                .orElseThrow(() -> new IllegalStateException("UserCredentials не найдены"));
        
        int currentCount = freshCredentials.getFailedLoginCount() != null ? freshCredentials.getFailedLoginCount() : 0;
        freshCredentials.setFailedLoginCount(currentCount + 1);
        freshCredentials.setLastFailedLoginAt(LocalDateTime.now());
        
        // Сохранение произойдет автоматически в конце транзакции
        log.info("Увеличен счетчик неверных попыток для пользователя ID: {}. Текущее значение: {}", 
            freshCredentials.getUser().getId(), freshCredentials.getFailedLoginCount());
    }

    /**
     * Сбросить счетчик неверных попыток входа при успешном логине
     * Использует текущую транзакцию, так как вызывается при успешном логине
     */
    private void resetFailedLoginCount(UserCredentials credentials) {
        if (credentials.getFailedLoginCount() != null && credentials.getFailedLoginCount() > 0) {
            log.info("Сброс счетчика неверных попыток для пользователя ID: {}. Было: {}", 
                credentials.getUser().getId(), credentials.getFailedLoginCount());
            credentials.setFailedLoginCount(0);
            credentials.setLastFailedLoginAt(null);
            // Сохранение произойдет автоматически в конце транзакции login()
        }
    }
}
