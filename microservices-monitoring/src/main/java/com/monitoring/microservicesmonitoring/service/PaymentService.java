package com.monitoring.microservicesmonitoring.service;

import com.monitoring.microservicesmonitoring.model.PaymentRecord;
import com.monitoring.microservicesmonitoring.proxy.ExecutableService;
import com.monitoring.microservicesmonitoring.store.PaymentStore;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PaymentService implements ExecutableService<Object> {

    private final PaymentStore paymentStore;

    public PaymentService(PaymentStore paymentStore) {
        this.paymentStore = paymentStore;
    }

    public List<PaymentRecord> getAllPayments() {
        return paymentStore.findAll();
    }

    public PaymentRecord getPaymentById(Long id) {
        return paymentStore.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with id: " + id));
    }

    public PaymentRecord processPayment(Long orderId, BigDecimal amount, String method) {
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("orderId must be greater than zero");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }
        if (method == null || method.isBlank()) {
            throw new IllegalArgumentException("method is required");
        }

        if (shouldFailRandomly()) {
            throw new RuntimeException("Payment was rejected by simulated provider");
        }

        PaymentRecord payment = PaymentRecord.builder()
                .orderId(orderId)
                .amount(amount)
                .method(method)
                .status("APPROVED")
                .createdAt(LocalDateTime.now())
                .paymentReference("PAY-" + (System.currentTimeMillis() % 1_000_000))
                .build();

        paymentStore.save(payment);
        return payment;
    }

    @Override
    public Object execute(String operation, Object... params) {
        return switch (operation) {
            case "getAllPayments" -> getAllPayments();
            case "getPaymentById" -> getPaymentById(requireLongParam(params, 0, "id"));
            case "processPayment" -> processPayment(
                    requireLongParam(params, 0, "orderId"),
                    requireBigDecimalParam(params, 1, "amount"),
                    requireStringParam(params, 2, "method")
            );
            default -> throw new IllegalArgumentException("Unsupported operation: " + operation);
        };
    }

    private boolean shouldFailRandomly() {
        return ThreadLocalRandom.current().nextInt(10) == 0;
    }

    private String requireStringParam(Object[] params, int index, String name) {
        if (params == null || index >= params.length || params[index] == null) {
            throw new IllegalArgumentException("Missing parameter: " + name);
        }
        return String.valueOf(params[index]);
    }

    private Long requireLongParam(Object[] params, int index, String name) {
        if (params == null || index >= params.length || params[index] == null) {
            throw new IllegalArgumentException("Missing parameter: " + name);
        }
        Object value = params[index];
        if (value instanceof Long longValue) {
            return longValue;
        }
        if (value instanceof Number numberValue) {
            return numberValue.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid long for parameter: " + name);
        }
    }

    private BigDecimal requireBigDecimalParam(Object[] params, int index, String name) {
        if (params == null || index >= params.length || params[index] == null) {
            throw new IllegalArgumentException("Missing parameter: " + name);
        }
        Object value = params[index];
        if (value instanceof BigDecimal bigDecimalValue) {
            return bigDecimalValue;
        }
        try {
            return new BigDecimal(String.valueOf(value));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid decimal for parameter: " + name);
        }
    }
}
