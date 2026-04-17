package com.monitoring.microservicesmonitoring.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItem {

    private String sku;
    private String name;
    private Integer stock;
    private BigDecimal price;
}
