package com.monitoring.microservicesmonitoring.controller;

import com.monitoring.microservicesmonitoring.dto.ServiceOperationRequestDTO;
import com.monitoring.microservicesmonitoring.proxy.MicroserviceProxy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final MicroserviceProxy<Object> inventoryProxy;
    private final MicroserviceProxy<Object> orderProxy;
    private final MicroserviceProxy<Object> paymentProxy;

    public ServiceController(
            @Qualifier("inventoryProxy") MicroserviceProxy<Object> inventoryProxy,
            @Qualifier("orderProxy") MicroserviceProxy<Object> orderProxy,
            @Qualifier("paymentProxy") MicroserviceProxy<Object> paymentProxy
    ) {
        this.inventoryProxy = inventoryProxy;
        this.orderProxy = orderProxy;
        this.paymentProxy = paymentProxy;
    }

    @PostMapping("/inventory/{operation}")
    public ResponseEntity<?> executeInventory(
            @PathVariable String operation,
            @RequestBody ServiceOperationRequestDTO request
    ) {
        return executeWithProxy(inventoryProxy, operation, request);
    }

    @PostMapping("/orders/{operation}")
    public ResponseEntity<?> executeOrders(
            @PathVariable String operation,
            @RequestBody ServiceOperationRequestDTO request
    ) {
        return executeWithProxy(orderProxy, operation, request);
    }

    @PostMapping("/payments/{operation}")
    public ResponseEntity<?> executePayments(
            @PathVariable String operation,
            @RequestBody ServiceOperationRequestDTO request
    ) {
        return executeWithProxy(paymentProxy, operation, request);
    }

    private ResponseEntity<?> executeWithProxy(
            MicroserviceProxy<Object> proxy,
            String operation,
            ServiceOperationRequestDTO request
    ) {
        Object[] params = request == null || request.getParams() == null ? new Object[0] : request.getParams().toArray();

        try {
            Object result = proxy.execute(operation, params);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error while executing operation");
        }
    }
}
