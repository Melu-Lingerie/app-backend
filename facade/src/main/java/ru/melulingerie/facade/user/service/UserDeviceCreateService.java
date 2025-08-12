package ru.melulingerie.facade.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melulingerie.facade.user.mapper.UserFacadeMapper.UserCreateInternalDto.DeviceInfoDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDeviceCreateService {
    
    public Long createUserDevice(DeviceInfoDto deviceInfo, Long userId) {
        // Заглушка - в реальной реализации будет создаваться устройство в модуле users
        log.info("Создание устройства для пользователя: {} с deviceUuid: {}", userId, deviceInfo.getDeviceUuid());
        return 200L + userId;
    }
}
