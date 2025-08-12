package ru.mellingerie.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mellingerie.users.entity.UserDevice;
import ru.mellingerie.users.repository.UserDeviceRepository;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDeviceQueryService {
    
    private final UserDeviceRepository userDeviceRepository;
    
    public Optional<UserDevice> findByDeviceUuid(UUID deviceUuid) {
        log.debug("Поиск устройства по deviceUuid: {}", deviceUuid);
        return userDeviceRepository.findByDeviceUuid(deviceUuid);
    }
    
    public Optional<UserDevice> findByUserIdAndDeviceUuid(Long userId, UUID deviceUuid) {
        log.debug("Поиск устройства по userId: {} и deviceUuid: {}", userId, deviceUuid);
        return userDeviceRepository.findByUserIdAndDeviceUuid(userId, deviceUuid);
    }
}
