package com.monitoring.microservicesmonitoring.service;

import com.monitoring.microservicesmonitoring.model.InventoryItem;
import com.monitoring.microservicesmonitoring.proxy.ExecutableService;
import com.monitoring.microservicesmonitoring.store.InventoryStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService implements ExecutableService<Object> {

    private final InventoryStore inventoryStore;

    public InventoryService(InventoryStore inventoryStore) {
        this.inventoryStore = inventoryStore;
    }

    public List<InventoryItem> getAllItems() {
        return inventoryStore.findAll();
    }

    public InventoryItem getItemBySku(String sku) {
        return inventoryStore.findBySku(sku)
                .orElseThrow(() -> new IllegalArgumentException("Item not found for sku: " + sku));
    }

    public InventoryItem reserveStock(String sku, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        InventoryItem item = getItemBySku(sku);
        int currentStock = item.getStock() == null ? 0 : item.getStock();
        if (currentStock < quantity) {
            throw new IllegalArgumentException("Insufficient stock for sku: " + sku);
        }

        item.setStock(currentStock - quantity);
        inventoryStore.save(item);
        return item;
    }

    public InventoryItem releaseStock(String sku, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        InventoryItem item = getItemBySku(sku);
        int currentStock = item.getStock() == null ? 0 : item.getStock();
        item.setStock(currentStock + quantity);
        inventoryStore.save(item);
        return item;
    }

    @Override
    public Object execute(String operation, Object... params) {
        return switch (operation) {
            case "getAllItems" -> getAllItems();
            case "getItemBySku" -> getItemBySku(requireStringParam(params, 0, "sku"));
            case "reserveStock" -> reserveStock(
                    requireStringParam(params, 0, "sku"),
                    requireIntegerParam(params, 1, "quantity")
            );
            case "releaseStock" -> releaseStock(
                    requireStringParam(params, 0, "sku"),
                    requireIntegerParam(params, 1, "quantity")
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
}
