package com.monitoring.microservicesmonitoring.service;

import com.monitoring.microservicesmonitoring.model.OrderRecord;
import com.monitoring.microservicesmonitoring.proxy.ExecutableService;
import com.monitoring.microservicesmonitoring.store.OrderStore;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService implements ExecutableService<Object> {

    private final OrderStore orderStore;

    public OrderService(OrderStore orderStore) {
        this.orderStore = orderStore;
    }

    public List<OrderRecord> getAllOrders() {
        return orderStore.findAll();
    }

    public OrderRecord getOrderById(Long id) {
        return orderStore.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
    }

    public OrderRecord createOrder(String customerName, String sku, Integer quantity, BigDecimal total) {
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("customerName is required");
        }
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("sku is required");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
        if (total == null || total.signum() < 0) {
            throw new IllegalArgumentException("total must be zero or positive");
        }

        OrderRecord order = OrderRecord.builder()
                .customerName(customerName)
                .sku(sku)
                .quantity(quantity)
                .total(total)
                .status("CREATED")
                .createdAt(LocalDateTime.now())
                .orderNumber("ORD-" + (System.currentTimeMillis() % 1_000_000))
                .build();

        orderStore.save(order);
        return order;
    }

    public OrderRecord updateOrderStatus(Long id, String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("status is required");
        }

        OrderRecord order = getOrderById(id);
        order.setStatus(status);
        orderStore.save(order);
        return order;
    }

    @Override
    public Object execute(String operation, Object... params) {
        return switch (operation) {
            case "getAllOrders" -> getAllOrders();
            case "getOrderById" -> getOrderById(requireLongParam(params, 0, "id"));
            case "createOrder" -> createOrder(
                    requireStringParam(params, 0, "customerName"),
                    requireStringParam(params, 1, "sku"),
                    requireIntegerParam(params, 2, "quantity"),
                    requireBigDecimalParam(params, 3, "total")
            );
            case "updateOrderStatus" -> updateOrderStatus(
                    requireLongParam(params, 0, "id"),
                    requireStringParam(params, 1, "status")
            );
            default -> throw new IllegalArgumentException("Unsupported operation: " + operation);
        };
    }

    private String requireStringParam(Object[] params, int index, String name) {
        if (params == null || index >= params.length || params[index] == null) {
            throw new IllegalArgumentException("Missing parameter: " + name);
        }
        return String.valueOf(params[index]);
    }

    private Integer requireIntegerParam(Object[] params, int index, String name) {
        if (params == null || index >= params.length || params[index] == null) {
            throw new IllegalArgumentException("Missing parameter: " + name);
        }
        Object value = params[index];
        if (value instanceof Integer integerValue) {
            return integerValue;
        }
        if (value instanceof Number numberValue) {
            return numberValue.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid integer for parameter: " + name);
        }
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
