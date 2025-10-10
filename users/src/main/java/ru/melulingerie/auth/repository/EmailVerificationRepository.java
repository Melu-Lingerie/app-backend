package ru.melulingerie.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.melulingerie.auth.entity.VerificationCode;
import ru.melulingerie.auth.entity.VerificationCode.VerificationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<VerificationCode, Long> {

    // Основные методы для гибридного подхода
    @Query("SELECT ev FROM VerificationCode ev JOIN FETCH ev.userCredentials uc JOIN FETCH uc.user u WHERE uc.identityType = 'EMAIL' AND uc.identifier = :email")
    Optional<VerificationCode> findTopByEmailOrderByIdDesc(String email);
    
    // Для tolerance логики - поиск недавних кодов
    @Query("SELECT ev FROM VerificationCode ev WHERE ev.userCredentials.identifier = :email AND ev.createdAt >= :since ORDER BY ev.id DESC")
    List<VerificationCode> findRecentByEmail(String email, LocalDateTime since);
    
    // Отмечаем старые коды как замененные (при отправке нового)
    @Modifying
    @Query("UPDATE VerificationCode ev SET ev.status = :newStatus WHERE ev.userCredentials.user.id = :userId AND ev.status = :currentStatus")
    void updateStatusByUserIdAndCurrentStatus(Long userId, VerificationStatus currentStatus, VerificationStatus newStatus);
    
    // Простая очистка - удаляем коды старше N часов
    @Modifying
    @Query("DELETE FROM VerificationCode ev WHERE ev.expiresAt < :cutoffTime")
    void deleteOldCodes(LocalDateTime cutoffTime);

    // Обратная совместимость (постепенно удалим)
    @Modifying
    void deleteByUserCredentials_User_Id(Long userId);
}
