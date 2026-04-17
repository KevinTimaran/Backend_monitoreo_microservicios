package com.monitoring.microservicesmonitoring.config;

import com.monitoring.microservicesmonitoring.proxy.LoggingProxy;
import com.monitoring.microservicesmonitoring.proxy.MicroserviceProxy;
import com.monitoring.microservicesmonitoring.service.AuditLogService;
import com.monitoring.microservicesmonitoring.service.InventoryService;
import com.monitoring.microservicesmonitoring.service.OrderService;
import com.monitoring.microservicesmonitoring.service.PaymentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProxyConfiguration {

    @Bean("inventoryProxy")
    public MicroserviceProxy<Object> inventoryProxy(
            InventoryService inventoryService,
            AuditLogService auditLogService
    ) {
        return new LoggingProxy<>("inventory", inventoryService, auditLogService);
    }

    @Bean("orderProxy")
    public MicroserviceProxy<Object> orderProxy(
            OrderService orderService,
            AuditLogService auditLogService
    ) {
        return new LoggingProxy<>("orders", orderService, auditLogService);
    }

    @Bean("paymentProxy")
    public MicroserviceProxy<Object> paymentProxy(
            PaymentService paymentService,
            AuditLogService auditLogService
    ) {
        return new LoggingProxy<>("payments", paymentService, auditLogService);
    }
}

