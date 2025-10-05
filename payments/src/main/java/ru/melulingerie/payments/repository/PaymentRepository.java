package ru.melulingerie.payments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.melulingerie.payments.domain.Payment;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByAcquirerPaymentId(String acquirerPaymentId);

    Optional<Payment> findByOrderId(UUID orderId);
}