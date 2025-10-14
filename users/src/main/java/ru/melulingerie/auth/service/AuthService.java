package ru.melulingerie.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.auth.dto.LoginRequestDto;
import ru.melulingerie.auth.dto.LoginResponseDto;
import ru.melulingerie.auth.dto.RefreshRequestDto;
import ru.melulingerie.auth.dto.RefreshResponseDto;
import ru.melulingerie.auth.dto.RegisterRequestDto;
import ru.melulingerie.auth.dto.VerifyEmailRequestDto;
import ru.melulingerie.auth.dto.VerifyEmailResponseDto;
import ru.melulingerie.auth.dto.ForgotPasswordRequestDto;
import ru.melulingerie.auth.dto.ForgotPasswordResponseDto;
import ru.melulingerie.auth.dto.ResetPasswordRequestDto;
import ru.melulingerie.auth.entity.VerificationCode;
import ru.melulingerie.auth.entity.RefreshToken;
import ru.melulingerie.auth.repository.EmailVerificationRepository;
import ru.melulingerie.auth.repository.RefreshTokenRepository;
import ru.melulingerie.auth.repository.UserCredentialsRepository;
import ru.melulingerie.users.entity.IdentityType;
import ru.melulingerie.users.entity.User;
import ru.melulingerie.users.entity.UserCredentials;
import ru.melulingerie.users.entity.UserStatus;
import ru.melulingerie.users.service.UserCreateService;

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
    private final EmailVerificationRepository emailVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailVerificationService emailVerificationService;
    private final UserCreateService userCreateService;
    private final TokenHashService tokenHashService;

    @Transactional
    public LoginResponseDto login(LoginRequestDto dto) {
        // 1) Найти учетные данные по email
        UserCredentials creds = credentialsRepository
                .findByIdentifierAndIdentityType(dto.getEmail(), IdentityType.EMAIL)
                .orElseThrow(() -> new IllegalArgumentException("Неверный email или пароль"));

        User user = creds.getUser();

        // 2) Проверка статуса/верификации
        if (user.getStatus() != UserStatus.ACTIVE || Boolean.FALSE.equals(creds.getIsVerified())) {
            //TODO заменить стандартные ошибки
            throw new IllegalStateException("Профиль не активирован или email не подтвержден");
        }

        // 3) Сверка пароля (BCrypt)
        if (creds.getPasswordHash() == null || !passwordEncoder.matches(dto.getPassword(), creds.getPasswordHash())) {
            // Увеличиваем счетчик неверных попыток
            //TODO не работает из-за транзакции
            incrementFailedLoginCount(creds);
            log.warn("Неверный пароль для пользователя с email: {}. Количество неверных попыток: {}", 
                dto.getEmail(), creds.getFailedLoginCount());
            throw new IllegalArgumentException("Неверный email или пароль");
        }

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
                // Email уже подтвержден - регистрация невозможна. TODO Рассмотреть вариант перенаправления на login на фронте.
                log.warn("Попытка регистрации с уже подтвержденным email: {}", dto.getEmail());
                throw new IllegalArgumentException("Пользователь с таким email уже зарегистрирован");
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
        UserCredentials userCredentials = createUserCredentials(user, dto, existingCreds);

        // 4. Отправить код верификации (старые коды удаляются автоматически)
        emailVerificationService.sendVerificationCode(dto.getEmail(), userCredentials);

        log.info("Регистрация инициирована для пользователя: {}, код отправлен на email: {}",
                user.getId(), dto.getEmail());
    }

    @Transactional
    public VerifyEmailResponseDto verifyEmailAndComplete(VerifyEmailRequestDto dto) {
        log.info("Подтверждение email: {}", dto.getEmail());

        // 1. Найти и проверить код верификации
        VerificationCode verification = emailVerificationService.validateCode(dto.getEmail(), dto.getCode());

        // 2. Активировать пользователя через UserCreateService
        User user = userCreateService.activateUser(verification.getUser());
        
        // 3. Пометить email как подтвержденный
        markCredentialsAsVerified(dto.getEmail());
        
        // 4. Удалить использованный код
        emailVerificationRepository.delete(verification);

        log.info("Email подтвержден и пользователь активирован: {}", user.getId());
        
        return VerifyEmailResponseDto.builder()
                .userId(user.getId())
                .isVerified(true)
                .build();
    }

    @Transactional
    public void resendVerificationCode(String email) {
        log.info("Повторная отправка кода для email: {}", email);

        // Найти неподтвержденного пользователя по email
        UserCredentials userCredentials = findUnverifiedUserByEmail(email);

        // Отправить новый код
        emailVerificationService.sendVerificationCode(email, userCredentials);
    }

    private void markCredentialsAsVerified(String email) {
        UserCredentials emailCreds = credentialsRepository
                .findByIdentifierAndIdentityType(email, IdentityType.EMAIL)
                .orElseThrow(() -> new IllegalStateException("Email credentials не найдены"));

        emailCreds.setIsVerified(true);
        emailCreds.setVerifiedAt(LocalDateTime.now());
        credentialsRepository.save(emailCreds);
    }

    private UserCredentials createUserCredentials(User user, RegisterRequestDto dto, Optional<UserCredentials> existingCreds) {
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

        return emailCreds;
    }

    private LoginResponseDto createLoginResponse(User user, String email, UUID sessionId) {
        UserCredentials creds = credentialsRepository
                .findByIdentifierAndIdentityType(email, IdentityType.EMAIL)
                .orElseThrow(() -> new IllegalStateException("Email credentials не найдены"));


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

    private UserCredentials findUnverifiedUserByEmail(String email) {
        UserCredentials creds = credentialsRepository
                .findByIdentifierAndIdentityType(email, IdentityType.EMAIL)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с таким email не найден"));

        User user = creds.getUser();
        if (user.getStatus() != UserStatus.PENDING_VERIFICATION || Boolean.TRUE.equals(creds.getIsVerified())) {
            throw new IllegalStateException("Пользователь уже подтвержден или имеет некорректный статус");
        }

        return creds;
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

    /**
     * Logout с текущего устройства - удаляет конкретный refresh token
     */
    @Transactional
    public void logout(String refreshToken) {
        // Извлекаем userId из refresh token
        Long userId;
        try {
            userId = jwtService.extractUserId(refreshToken);
        } catch (Exception e) {
            log.warn("Невозможно извлечь userId из refresh token при logout: {}", e.getMessage());
            throw new IllegalArgumentException("Неверный refresh token");
        }
        
        log.info("Logout: начало процесса для пользователя: {}", userId);
        
        List<RefreshToken> userTokens = refreshTokenRepository
            .findByUserIdAndIsRevokedFalseOrderByCreatedAtDesc(userId);
        
        boolean tokenFound = userTokens.stream()
            .filter(rt -> tokenHashService.matches(refreshToken, rt.getTokenHash()))
            .findFirst()
            .map(rt -> {
                refreshTokenRepository.delete(rt);
                log.info("Logout: удален refresh token для пользователя: {}", userId);
                return true;
            })
            .orElse(false);
        
        if (!tokenFound) {
            log.warn("Logout: refresh token не найден для пользователя: {}", userId);
        }
    }

    /**
     * Logout со всех устройств - удаляет все refresh токены пользователя
     */
    @Transactional
    public void logoutFromAllDevices(String refreshToken) {
        // Извлекаем userId из refresh token
        Long userId;
        try {
            userId = jwtService.extractUserId(refreshToken);
        } catch (Exception e) {
            log.warn("Невозможно извлечь userId из refresh token при logout-all: {}", e.getMessage());
            throw new IllegalArgumentException("Неверный refresh token");
        }
        
        log.info("Logout All: начало процесса для пользователя: {}", userId);
        
        refreshTokenRepository.deleteByUserId(userId);
        log.info("Logout All: удалены все refresh токены для пользователя: {}", userId);
    }

    /**
     * Запрос на сброс пароля - отправка OTP кода на email
     */
    @Transactional
    public ForgotPasswordResponseDto requestPasswordReset(ForgotPasswordRequestDto dto) {
        log.info("Запрос на сброс пароля для email: {}", dto.getEmail());
        
        // Найти пользователя по email
        UserCredentials credentials = credentialsRepository
                .findByIdentifierAndIdentityType(dto.getEmail(), IdentityType.EMAIL)
                .orElseThrow(() -> {
                    // Для безопасности логируем, но не раскрываем факт отсутствия email
                    log.warn("Попытка сброса пароля для несуществующего email: {}", dto.getEmail());
                    // Возвращаем общее исключение, чтобы не раскрывать информацию о существовании email
                    throw new IllegalArgumentException("Если email существует в системе, код был отправлен");
                });
        
        User user = credentials.getUser();
        
        // Проверка: пользователь должен быть активным
        if (user.getStatus() != UserStatus.ACTIVE) {
            log.warn("Попытка сброса пароля для неактивного пользователя: {}", user.getId());
            throw new IllegalStateException("Аккаунт не активирован. Пожалуйста, завершите регистрацию");
        }
        
        // Проверка: email должен быть подтвержден
        if (Boolean.FALSE.equals(credentials.getIsVerified())) {
            log.warn("Попытка сброса пароля для неподтвержденного email: {}", dto.getEmail());
            throw new IllegalStateException("Email не подтвержден. Пожалуйста, завершите регистрацию");
        }
        
        // Отправить код верификации для сброса пароля
        // EmailVerificationService автоматически обработает:
        // - cooldown (не чаще раза в минуту)
        // - supersede старых кодов
        // - создание нового кода с TTL 15 минут
        emailVerificationService.sendPasswordResetCode(dto.getEmail(), credentials);
        
        log.info("Код для сброса пароля отправлен на email: {} для пользователя: {}", 
            dto.getEmail(), user.getId());
        
        return ForgotPasswordResponseDto.builder()
                .message("Код для сброса пароля отправлен на email")
                .email(dto.getEmail())
                .codeExpiresInMinutes(15)
                .build();
    }

    /**
     * Сброс пароля с использованием OTP кода
     */
    @Transactional
    public void resetPassword(ResetPasswordRequestDto dto) {
        log.info("Попытка сброса пароля для email: {}", dto.getEmail());
        
        // 1. Валидировать код через существующую логику
        // Это автоматически проверит: TTL, attempts, status
        VerificationCode verification = emailVerificationService.validateCode(dto.getEmail(), dto.getCode());
        
        // 2. Найти credentials
        UserCredentials credentials = credentialsRepository
                .findByIdentifierAndIdentityType(dto.getEmail(), IdentityType.EMAIL)
                .orElseThrow(() -> new IllegalStateException("Учетные данные не найдены"));
        
        User user = credentials.getUser();
        
        // 3. Обновить пароль
        credentials.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        credentials.setFailedLoginCount(0);
        credentials.setLastFailedLoginAt(null);
        credentialsRepository.save(credentials);
        
        log.info("Пароль обновлен для пользователя: {}", user.getId());
        
        // 4. ВАЖНО: Выйти со всех устройств для безопасности
        // Если кто-то украл access token, он станет недействительным после смены пароля
         refreshTokenRepository.deleteByUserId(user.getId());
        log.info("Удаление refresh токенов для пользователя: {} (logout со всех устройств после сброса пароля)",
             user.getId());
        
        // 5. Удалить использованный код верификации
        emailVerificationRepository.delete(verification);
        
        log.info("Пароль успешно сброшен для пользователя: {}. Требуется повторный вход", user.getId());
    }
}
