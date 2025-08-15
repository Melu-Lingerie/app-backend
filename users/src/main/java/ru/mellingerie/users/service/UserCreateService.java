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
        return userSessionQueryService.findBySessionId(request.getSessionId())
                .map(existingSession -> handleExistingSession(existingSession))
                .orElseGet(() -> createNewUserWithSessionAndDevice(request));
    }

    private UserCreateResponseDto handleExistingSession(UserSession existingSession) {
        User user = existingSession.getUser();
        log.debug("Using existing session {} for user {}",
                existingSession.getId(), user.getId());

        return buildUserResponse(user);
    }

    private UserCreateResponseDto createNewUserWithSessionAndDevice(UserCreateRequestDto request) {
        User newUser = createAndSaveUser();
        userSessionCreateService.createUserSession(request.getSessionId(), newUser,request.getDeviceInfo(),
                extractIpAddress(request.getDeviceInfo()));

        log.info("Created new user with ID: {}", newUser.getId());
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

        return UserCreateResponseDto.builder()
                .userId(user.getId())
                .cartId(123L)
                .wishlistId(123L)
                .build();
    }

    private String extractIpAddress(UserCreateRequestDto.DeviceInfoDto deviceInfo) {
        return deviceInfo != null ? deviceInfo.getIpAddress() : null;
    }
}