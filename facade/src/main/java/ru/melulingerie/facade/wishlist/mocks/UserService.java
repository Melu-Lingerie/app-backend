package ru.melulingerie.facade.wishlist.mocks;

import ru.melulingerie.facade.wishlist.mocks.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserById(Long id);
}