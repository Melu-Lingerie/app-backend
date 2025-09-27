package ru.melulingerie.auth.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.melulingerie.users.entity.User;
import ru.melulingerie.users.entity.UserCredentials;
import ru.melulingerie.users.entity.UserStatus;

import java.util.Collection;
import java.util.List;

public class CustomUserPrincipal implements UserDetails {
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