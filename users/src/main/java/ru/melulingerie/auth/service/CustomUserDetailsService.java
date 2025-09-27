package ru.melulingerie.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.auth.entity.CustomUserPrincipal;
import ru.melulingerie.auth.repository.UserCredentialsRepository;
import ru.melulingerie.users.entity.IdentityType;
import ru.melulingerie.users.entity.UserCredentials;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserCredentialsRepository userCredentialsRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserCredentials credentials = userCredentialsRepository
                .findByIdentifierAndIdentityType(email, IdentityType.EMAIL)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + email));

        return new CustomUserPrincipal(credentials);
    }
}