package ru.melulingerie.api.payments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.melulingerie.facade.payments.service.ManualYooKassaFacadeService;
import ru.melulingerie.facade.payments.dto.TestPaymentRequest;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payments/test")
@RequiredArgsConstructor
public class PaymentTestController {

    private final ManualYooKassaFacadeService manualYooKassaFacadeService;

    @PostMapping("/manual")
    public ResponseEntity<Map<String, Object>> testManualPayment(@RequestBody TestPaymentRequest request) {
        log.info("Testing manual payment creation: {}", request);

        Map<String, Object> response = manualYooKassaFacadeService.createPaymentManually(request);

        return ResponseEntity.ok(response);
    }
}