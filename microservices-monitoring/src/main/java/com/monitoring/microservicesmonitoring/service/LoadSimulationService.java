package com.monitoring.microservicesmonitoring.service;

import com.monitoring.microservicesmonitoring.dto.SimulationResultDTO;
import com.monitoring.microservicesmonitoring.proxy.MicroserviceProxy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class LoadSimulationService {

    private static final int TOTAL_CALLS = 50;

    private final MicroserviceProxy<Object> inventoryProxy;
    private final MicroserviceProxy<Object> orderProxy;
    private final MicroserviceProxy<Object> paymentProxy;

    public LoadSimulationService(
            @Qualifier("inventoryProxy") MicroserviceProxy<Object> inventoryProxy,
            @Qualifier("orderProxy") MicroserviceProxy<Object> orderProxy,
            @Qualifier("paymentProxy") MicroserviceProxy<Object> paymentProxy
    ) {
        this.inventoryProxy = inventoryProxy;
        this.orderProxy = orderProxy;
        this.paymentProxy = paymentProxy;
    }

    public SimulationResultDTO simulateLoad() {
        long startedAt = System.currentTimeMillis();
        int success = 0;
        int errors = 0;

        for (int i = 0; i < TOTAL_CALLS; i++) {
            try {
                runRandomOperation();
                success++;
            } catch (RuntimeException ex) {
                errors++;
            }
        }

        long duration = System.currentTimeMillis() - startedAt;
        return new SimulationResultDTO(
                TOTAL_CALLS,
                success,
                errors,
                duration,
                "Simulation completed"
        );
    }

    private void runRandomOperation() {
        int option = ThreadLocalRandom.current().nextInt(5);

        switch (option) {
            case 0 -> inventoryProxy.execute("getAllItems");
            case 1 -> inventoryProxy.execute("getItemBySku", randomSku());
            case 2 -> orderProxy.execute("getAllOrders");
            case 3 -> orderProxy.execute("createOrder", "Customer-" + randomInt(1, 100), randomSku(), 1, new BigDecimal("49.99"));
            case 4 -> paymentProxy.execute("processPayment", (long) randomInt(1, 30), new BigDecimal("49.99"), "CARD");
            default -> throw new IllegalArgumentException("Unsupported simulation option");
        }
    }

    private String randomSku() {
        return "SKU-100" + randomInt(1, 3);
    }

    private int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
