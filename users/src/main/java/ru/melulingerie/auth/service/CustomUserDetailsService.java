package ru.melulingerie.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.melulingerie.users.entity.IdentityType;
import ru.melulingerie.users.entity.User;
import ru.melulingerie.users.entity.UserCredentials;
import ru.melulingerie.users.entity.UserStatus;
import ru.melulingerie.auth.repository.UserCredentialsRepository;

import java.util.Collection;
import java.util.List;

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

    public static class CustomUserPrincipal implements UserDetails {
        private final UserCredentials userCredentials;
        private final User user;

        public CustomUserPrincipal(UserCredentials userCredentials) {
            this.userCredentials = userCredentials;
            this.user = userCredentials.getUser();
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        }

        @Override
        public String getPassword() {
            return userCredentials.getPasswordHash();
        }

        @Override
        public String getUsername() {
            return userCredentials.getIdentifier();
        }

        @Override
        public boolean isAccountNonExpired() {
            return user.getStatus() != UserStatus.BANNED;
        }

        @Override
        public boolean isAccountNonLocked() {
            return userCredentials.getFailedLoginCount() < 5;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return user.getStatus() == UserStatus.ACTIVE && Boolean.TRUE.equals(userCredentials.getIsVerified());
        }

        public Long getUserId() {
            return user.getId();
        }

        public User getUser() {
            return user;
        }

        public UserCredentials getUserCredentials() {
            return userCredentials;
        }
    }
}

