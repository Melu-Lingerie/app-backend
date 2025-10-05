package ru.melulingerie.api.payments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.melulingerie.facade.payments.dto.request.PaymentCancelFacadeRequestDto;
import ru.melulingerie.facade.payments.dto.request.PaymentCreateFacadeRequestDto;
import ru.melulingerie.facade.payments.dto.response.PaymentFacadeResponseDto;
import ru.melulingerie.facade.payments.service.PaymentFacadeService;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentFacadeService paymentFacadeService;

    @PostMapping
    public ResponseEntity<PaymentFacadeResponseDto> createPayment(@Valid @RequestBody PaymentCreateFacadeRequestDto request) {
        PaymentFacadeResponseDto response = paymentFacadeService.createPayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentFacadeResponseDto> getPayment(@PathVariable Long paymentId) {
        PaymentFacadeResponseDto response = paymentFacadeService.getPaymentById(paymentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/external/{acquirerPaymentId}")
    public ResponseEntity<PaymentFacadeResponseDto> getPaymentByExternalId(@PathVariable String acquirerPaymentId) {
        PaymentFacadeResponseDto response = paymentFacadeService.getPaymentByAcquirerPaymentId(acquirerPaymentId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<PaymentFacadeResponseDto> cancelPayment(@PathVariable Long paymentId, @Valid @RequestBody PaymentCancelFacadeRequestDto request) {
        PaymentFacadeResponseDto response = paymentFacadeService.cancelPayment(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentFacadeResponseDto> refundPayment(@PathVariable Long paymentId) {
        PaymentFacadeResponseDto response = paymentFacadeService.refundPayment(paymentId);
        return ResponseEntity.ok(response);
    }
}