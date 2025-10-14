package ru.melulingerie.facade.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.melulingerie.auth.entity.CustomUserPrincipal;
import ru.melulingerie.facade.user.dto.UserInfoResponseDto;
import ru.melulingerie.facade.user.mapper.UserFacadeMapper;
import ru.melulingerie.users.entity.User;
import ru.melulingerie.users.service.UserQueryService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInfoFacadeServiceImpl implements UserInfoFacadeService {

    private final UserQueryService userQueryService;
    private final UserFacadeMapper userFacadeMapper;

    @Override
    public UserInfoResponseDto getUserInfo(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Пользователь не аутентифицирован");
        }
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserPrincipal customUserPrincipal)) {
            throw new IllegalStateException("Невозможно получить пользователя из контекста безопасности");
        }
        
        Long userId = customUserPrincipal.getUserId();
        log.info("Запрос информации о пользователе с ID: {}", userId);
        
        User user = userQueryService.getUserById(userId);
        
        return userFacadeMapper.toUserInfoResponseDto(user);
    }
}

