package ru.mellingerie.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mellingerie.users.entity.UserDevice;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
    
    Optional<UserDevice> findByDeviceUuid(UUID deviceUuid);
    
    Optional<UserDevice> findByUserIdAndDeviceUuid(Long userId, UUID deviceUuid);
} 