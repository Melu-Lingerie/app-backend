package ru.melulingerie.api.payments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.melulingerie.facade.payments.service.PaymentWebhookFacadeService;

@Slf4j
@RestController
@RequestMapping("/api/payments/webhooks")
@RequiredArgsConstructor
public class PaymentWebhookController {
    private final PaymentWebhookFacadeService webhookFacadeService;

    @PostMapping("/yookassa")
    public ResponseEntity<Void> handleYooKassaWebhook(@RequestBody String paymentJson,
                                                      @RequestHeader(value = "Authorization", required = false) String authorization) {
        log.info("Received YooKassa webhook with payload: {}", paymentJson);

        webhookFacadeService.processPaymentAcquirerWebhook(paymentJson, authorization);

        return ResponseEntity.ok().build();
    }
}