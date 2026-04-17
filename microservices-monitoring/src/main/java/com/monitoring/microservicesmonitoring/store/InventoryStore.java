package com.monitoring.microservicesmonitoring.store;

import com.monitoring.microservicesmonitoring.model.InventoryItem;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InventoryStore {

    private final Map<String, InventoryItem> inventoryBySku = new ConcurrentHashMap<>();

    public List<InventoryItem> findAll() {
        return new ArrayList<>(inventoryBySku.values());
    }

    public Optional<InventoryItem> findBySku(String sku) {
        if (sku == null || sku.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(inventoryBySku.get(sku));
    }

    public void save(InventoryItem item) {
        if (item == null || item.getSku() == null || item.getSku().isBlank()) {
            throw new IllegalArgumentException("Inventory item sku is required");
        }
        inventoryBySku.put(item.getSku(), item);
    }

    @PostConstruct
    public void preload() {
        if (!inventoryBySku.isEmpty()) {
            return;
        }

        save(InventoryItem.builder().sku("SKU-1001").name("Keyboard").stock(25).price(new BigDecimal("45.90")).build());
        save(InventoryItem.builder().sku("SKU-1002").name("Mouse").stock(40).price(new BigDecimal("19.99")).build());
        save(InventoryItem.builder().sku("SKU-1003").name("Monitor 24\"").stock(15).price(new BigDecimal("189.50")).build());
    }
}
