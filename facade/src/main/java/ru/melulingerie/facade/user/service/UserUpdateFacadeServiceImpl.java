package ru.melulingerie.facade.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.melulingerie.auth.entity.CustomUserPrincipal;
import ru.melulingerie.facade.user.dto.UserUpdateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserUpdateFacadeResponseDto;
import ru.melulingerie.facade.user.mapper.UserFacadeMapper;
import ru.melulingerie.users.dto.UserUpdateRequestDto;
import ru.melulingerie.users.entity.User;
import ru.melulingerie.users.service.UserUpdateService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserUpdateFacadeServiceImpl implements UserUpdateFacadeService {

    private final UserUpdateService userUpdateService;
    private final UserFacadeMapper userFacadeMapper;

    @Override
    public UserUpdateFacadeResponseDto updateCurrentUser(Authentication authentication, UserUpdateFacadeRequestDto request) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Пользователь не аутентифицирован");
        }
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserPrincipal customUserPrincipal)) {
            throw new IllegalStateException("Невозможно получить пользователя из контекста безопасности");
        }
        
        Long userId = customUserPrincipal.getUserId();
        log.info("Обновление данных пользователя с ID: {}", userId);
        
        // Преобразовать facade DTO в users DTO
        UserUpdateRequestDto userUpdateRequest = userFacadeMapper.toUserUpdateRequestDto(request);
        
        // Обновить пользователя
        User updatedUser = userUpdateService.updateUser(userId, userUpdateRequest);
        
        // Преобразовать в facade response
        return userFacadeMapper.toUserUpdateFacadeResponseDto(updatedUser);
    }
}

