package ru.mellingerie.facade.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import ru.mellingerie.facade.user.dto.UserCreateRequestDto;
import ru.mellingerie.facade.user.dto.UserCreateResponseDto;
import ru.mellingerie.facade.user.mapper.UserCreateRequestMapper;
import ru.mellingerie.facade.user.mapper.UserMapper;
import ru.mellingerie.users.dto.UserDeviceRequestDto;
import ru.mellingerie.users.entity.User;
import ru.mellingerie.users.entity.UserSession;
import ru.mellingerie.users.service.UserCreateService;
import ru.mellingerie.users.service.UserSessionCreateService;

@Slf4j
@RequiredArgsConstructor
public class UserCreateFacadeServiceImpl implements UserCreateFacadeService {
    
    private final UserCreateService userCreateService;
    private final UserSessionCreateService userSessionCreateService;
    private final UserMapper userMapper;
    private final UserCreateRequestMapper userCreateRequestMapper;

    @Override
    @Transactional
    public UserCreateResponseDto createUser(UserCreateRequestDto request) {
        log.info("Создание пользователя-гостя с sessionId: {}", request.getSessionId());

        User user = userCreateService.createGuestUser();

        UserDeviceRequestDto deviceRequestDto = userCreateRequestMapper.toUserDeviceRequestDto(request);

        UserSession userSession = userSessionCreateService.createUserSession(
                user,
                deviceRequestDto,
                request.getSessionId(),
                request.getIpAddress()
        );

        UserCreateResponseDto response = userMapper.toUserCreateResponseDto(user);
        response.setSessionId(userSession.getSessionId());

        log.info("Пользователь-гость успешно создан с ID: {}", user.getId());
        return response;
    }
}