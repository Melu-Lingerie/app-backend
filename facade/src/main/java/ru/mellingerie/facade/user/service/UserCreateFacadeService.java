package ru.mellingerie.facade.user.service;

import ru.mellingerie.facade.user.dto.UserCreateFacadeRequestDto;
import ru.mellingerie.facade.user.dto.UserCreateFacadeResponseDto;

public interface UserCreateFacadeService {

   UserCreateFacadeResponseDto createUser(UserCreateFacadeRequestDto request);
}
