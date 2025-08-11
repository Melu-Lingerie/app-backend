package ru.melulingerie.facade.user.service;

import ru.melulingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.melulingerie.facade.user.dto.UserCreateFacadeResponseDto;

public interface UserCreateFacadeService {

   UserCreateFacadeResponseDto createUser(UserCreateFacadeRequestDto request);
}
