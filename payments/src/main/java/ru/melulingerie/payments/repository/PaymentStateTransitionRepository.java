package ru.melulingerie.payments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.melulingerie.payments.domain.PaymentStateTransition;

import java.util.List;

@Repository
public interface PaymentStateTransitionRepository extends JpaRepository<PaymentStateTransition, Long> {

    List<PaymentStateTransition> findByPaymentIdOrderByCreatedAtAsc(Long paymentId);

    List<PaymentStateTransition> findByExternalPaymentIdOrderByCreatedAtAsc(String externalPaymentId);
}