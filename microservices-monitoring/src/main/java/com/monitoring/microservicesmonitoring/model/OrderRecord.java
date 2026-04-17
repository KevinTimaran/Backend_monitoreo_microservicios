package com.monitoring.microservicesmonitoring.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRecord {

    private Long id;
    private String orderNumber;
    private String customerName;
    private String sku;
    private Integer quantity;
    private BigDecimal total;
    private String status;
    private LocalDateTime createdAt;
}
