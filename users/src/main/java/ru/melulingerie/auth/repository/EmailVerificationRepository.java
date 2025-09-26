package ru.melulingerie.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.melulingerie.auth.entity.EmailVerification;
import ru.melulingerie.auth.entity.EmailVerification.VerificationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    // Основные методы для гибридного подхода
    Optional<EmailVerification> findTopByEmailOrderByIdDesc(String email);
    
    // Для tolerance логики - поиск недавних кодов
    @Query("SELECT ev FROM EmailVerification ev WHERE ev.email = :email AND ev.createdAt >= :since ORDER BY ev.id DESC")
    List<EmailVerification> findRecentByEmail(String email, LocalDateTime since);
    
    // Отмечаем старые коды как замененные (при отправке нового)
    @Modifying
    @Query("UPDATE EmailVerification ev SET ev.status = :newStatus WHERE ev.user.id = :userId AND ev.status = :currentStatus")
    void updateStatusByUserIdAndCurrentStatus(Long userId, VerificationStatus currentStatus, VerificationStatus newStatus);
    
    // Простая очистка - удаляем коды старше N часов
    @Modifying
    @Query("DELETE FROM EmailVerification ev WHERE ev.expiresAt < :cutoffTime")
    void deleteOldCodes(LocalDateTime cutoffTime);

    // Обратная совместимость (постепенно удалим)
    @Modifying
    void deleteByUserId(Long userId);
}
