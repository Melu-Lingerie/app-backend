package ru.mellingerie.facade.user.service;

import ru.mellingerie.facade.user.dto.UserCreateRequestDto;
import ru.mellingerie.facade.user.dto.UserCreateResponseDto;

public interface UserCreateFacadeService {

   UserCreateResponseDto createUser(UserCreateRequestDto request);
}
