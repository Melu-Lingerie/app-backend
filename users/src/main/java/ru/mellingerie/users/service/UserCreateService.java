package ru.mellingerie.users.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mellingerie.users.dto.UserCreateRequestDto;
import ru.mellingerie.users.dto.UserCreateResponseDto;
import ru.mellingerie.users.entity.User;
import ru.mellingerie.users.entity.UserRole;
import ru.mellingerie.users.entity.UserSession;
import ru.mellingerie.users.entity.UserStatus;
import ru.mellingerie.users.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCreateService {

    private final UserRepository userRepository;
    private final UserSessionQueryService userSessionQueryService;
    private final UserSessionCreateService userSessionCreateService;

    @Transactional
    public UserCreateResponseDto createGuestUser(@NonNull UserCreateRequestDto request) {
        return userSessionQueryService.findBySessionId(request.sessionId())
                .map(this::handleExistingSession)
                .orElseGet(() -> createNewUserWithSessionAndDevice(request));
    }

    private UserCreateResponseDto handleExistingSession(UserSession existingSession) {
        User user = existingSession.getUser();
        log.info("У пользователя userId: {}, уже существует сессия с id: {}",
                user.getId(), existingSession.getId());

        return buildUserResponse(user);
    }

    private UserCreateResponseDto createNewUserWithSessionAndDevice(UserCreateRequestDto request) {
        User newUser = createAndSaveUser();
        userSessionCreateService.createUserSession(request.sessionId(), newUser, request.deviceInfo());

        log.info("Создание нового юзера с ID: {}", newUser.getId());
        return buildUserResponse(newUser);
    }

    private User createAndSaveUser() {
        User newUser = User.builder()
                .role(UserRole.GUEST)
                .status(UserStatus.UNREGISTERED)
                .build();

        return userRepository.save(newUser);
    }

    private UserCreateResponseDto buildUserResponse(User user) {
        //Long cartId = cartService.getCartForUser(user.getId()).getId();
        //Long wishlistId = wishlistService.getWishlistForUser(user.getId()).getId();

        return new UserCreateResponseDto(
                user.getId(),
                123L,
                123L
        );
    }
}