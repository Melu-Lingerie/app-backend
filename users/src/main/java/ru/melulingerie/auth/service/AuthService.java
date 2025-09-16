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
import ru.melulingerie.users.repository.UserRepository;
import ru.melulingerie.users.repository.UserSessionRepository;
import ru.melulingerie.users.service.UserSessionQueryService;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserCredentialsRepository credentialsRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailVerificationService emailVerificationService;
    private final UserSessionQueryService userSessionQueryService;

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
            // TODO счетчики failedLogin добавим позже
            throw new IllegalArgumentException("Неверный email или пароль");
        }

        // 4) Создать и сохранить UserSession
        UserSession session = UserSession.builder()
                .sessionId(UUID.randomUUID())
                .user(user)
                .status(SessionStatus.ACTIVE)
                .build();
        session = userSessionRepository.save(session);

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
    //TODO подумать над разделением логики для методов изменения юзера и только чтения
    @Transactional
    public void registerUser(RegisterRequestDto dto) {
        log.info("Начинаем регистрацию пользователя с email: {}", dto.getEmail());

        // 1. Проверить, что email не занят активным пользователем
        if (credentialsRepository.existsByIdentifierAndIdentityType(dto.getEmail(), IdentityType.EMAIL)) {
            log.warn("Попытка регистрации с уже существующим email: {}", dto.getEmail());
            //TODO подумать над кодом ошибки , что бы код ошибки говорил о проблеме
            //TODO подумать надо выводом сообщения и проверкой. т.к. сейчас у пользователя, который не ввел проверочный код возвращается такая ошибка
            throw new IllegalArgumentException("ПОДУМОЙ");
        }
        //TODO подумать над удалением старого емэила со статусом PENDING_VERIFICATION , если один и тот же пользователь пытается зарегестрироваться с нового с тем же userId
        // 2. Найти или создать пользователя
        User user = createUser(dto);

        // 3. Обновить данные пользователя
        //TODO сравнить с макетами поля для регистрации
        updateUserData(user, dto);
        //TODO нужно ли удалять все коды верификации
        // 4. Очистить старые коды верификации для этого пользователя
        clearPreviousVerificationCodes(user);

        // 5. Создать учетные данные (без верификации)
        createUserCredentials(user, dto);

        // 6. Отправить код верификации
        emailVerificationService.sendVerificationCode(dto.getEmail(), user);

        log.info("Регистрация инициирована для пользователя: {}, код отправлен на email: {}",
                user.getId(), dto.getEmail());
    }

    @Transactional
    public LoginResponseDto verifyEmailAndComplete(VerifyEmailRequestDto dto) {
        log.info("Подтверждение email: {}", dto.getEmail());

        // 1. Найти и проверить код верификации
        EmailVerification verification = emailVerificationService.validateCode(dto.getEmail(), dto.getCode());

        // 2. Активировать пользователя
        User user = verification.getUser();
        activateUser(user, dto.getEmail());
        //TODO необходимо хранить коды некоторое время
        // 3. Удалить использованный код
        emailVerificationRepository.delete(verification);

        // 4. Автоматически залогинить пользователя
        LoginResponseDto response = createLoginResponse(user, dto.getEmail());

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
    //TODO подумать над названием
    private User createUser(RegisterRequestDto dto) {

        // Ищем существующего гостевого пользователя по sessionId
        return userSessionQueryService.findBySessionId(dto.getSessionId())
                .map(UserSession::getUser)
                .filter(user -> user.getRole() == UserRole.GUEST &&
                        (user.getStatus() == UserStatus.UNREGISTERED || user.getStatus() == UserStatus.PENDING_VERIFICATION))
                .orElseThrow(() -> new IllegalArgumentException("Гостевой пользователь не найден или уже зарегистрирован"));
    }

    private void updateUserData(User user, RegisterRequestDto dto) {
        user.setFirstName(dto.getFirstName());
        user.setMiddleName(dto.getMiddleName());
        user.setLastName(dto.getLastName());
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        userRepository.save(user);
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
    //TODO auth service начинает управлять моделью (UserDetailService), а должен заниматься только аутентификацией
    private void activateUser(User user, String email) {
        // Обновляем статус пользователя
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(UserRole.CUSTOMER);
        userRepository.save(user);

        // Помечаем email как подтвержденный
        UserCredentials emailCreds = credentialsRepository
                .findByIdentifierAndIdentityType(email, IdentityType.EMAIL)
                .orElseThrow(() -> new IllegalStateException("Email credentials не найдены"));

        emailCreds.setIsVerified(true);
        emailCreds.setVerifiedAt(LocalDateTime.now());
        credentialsRepository.save(emailCreds);
    }

    private LoginResponseDto createLoginResponse(User user, String email) {
        UserCredentials creds = credentialsRepository
                .findByIdentifierAndIdentityType(email, IdentityType.EMAIL)
                .orElseThrow(() -> new IllegalStateException("Email credentials не найдены"));

        // Создать и сохранить сессию
        UserSession session = UserSession.builder()
                .sessionId(UUID.randomUUID())
                .user(user)
                .status(SessionStatus.ACTIVE)
                .build();
        session = userSessionRepository.save(session);

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

    private void clearPreviousVerificationCodes(User user) {
        // Удаляем все существующие коды верификации для этого пользователя
        emailVerificationRepository.deleteByUserId(user.getId());
    }

}
