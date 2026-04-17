package com.monitoring.microservicesmonitoring.store;

import com.monitoring.microservicesmonitoring.model.PaymentRecord;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class PaymentStore {

    private final Map<Long, PaymentRecord> paymentsById = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    public List<PaymentRecord> findAll() {
        return new ArrayList<>(paymentsById.values());
    }

    public Optional<PaymentRecord> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(paymentsById.get(id));
    }

    public void save(PaymentRecord payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment is required");
        }
        if (payment.getId() == null) {
            payment.setId(idSequence.getAndIncrement());
        }
        if (payment.getCreatedAt() == null) {
            payment.setCreatedAt(LocalDateTime.now());
        }
        paymentsById.put(payment.getId(), payment);
    }
}
