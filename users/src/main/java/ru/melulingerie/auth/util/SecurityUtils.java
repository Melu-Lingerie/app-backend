package ru.melulingerie.auth.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.melulingerie.auth.entity.CustomUserPrincipal;

/**
 * Утилитный класс для работы с контекстом безопасности Spring Security
 */
public class SecurityUtils {

    private SecurityUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Получение ID текущего авторизованного пользователя
     * @return ID пользователя
     * @throws IllegalStateException если пользователь не авторизован
     */
    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Пользователь не аутентифицирован");
        }
        
        Object principal = auth.getPrincipal();
        
        if (principal instanceof CustomUserPrincipal customUserPrincipal) {
            return customUserPrincipal.getUserId();
        }
        
        throw new IllegalStateException("Невозможно получить userId из контекста безопасности");
    }
    
    /**
     * Получение текущего авторизованного пользователя
     * @return CustomUserPrincipal текущего пользователя
     * @throws IllegalStateException если пользователь не авторизован
     */
    public static CustomUserPrincipal getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Пользователь не аутентифицирован");
        }
        
        Object principal = auth.getPrincipal();
        
        if (principal instanceof CustomUserPrincipal customUserPrincipal) {
            return customUserPrincipal;
        }
        
        throw new IllegalStateException("Невозможно получить пользователя из контекста безопасности");
    }

    /**
     * Получение email текущего авторизованного пользователя
     * @return email пользователя
     * @throws IllegalStateException если пользователь не авторизован
     */
    public static String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Пользователь не аутентифицирован");
        }
        
        return auth.getName();
    }
}

