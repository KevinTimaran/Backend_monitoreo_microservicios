package com.monitoring.microservicesmonitoring.store;

import com.monitoring.microservicesmonitoring.model.OrderRecord;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class OrderStore {

    private final Map<Long, OrderRecord> ordersById = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    public List<OrderRecord> findAll() {
        return new ArrayList<>(ordersById.values());
    }

    public Optional<OrderRecord> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(ordersById.get(id));
    }

    public void save(OrderRecord order) {
        if (order == null) {
            throw new IllegalArgumentException("Order is required");
        }
        if (order.getId() == null) {
            order.setId(idSequence.getAndIncrement());
        }
        if (order.getCreatedAt() == null) {
            order.setCreatedAt(LocalDateTime.now());
        }
        ordersById.put(order.getId(), order);
    }
}
